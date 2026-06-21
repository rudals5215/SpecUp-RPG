package com.specuprpg.global.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// @Primary = AiService 인터페이스 구현체가 여러 개일 때
// "지금은 이걸 기본으로 써"라고 알려주는 표시예요
// 나중에 ClaudeAiService 추가해도 이 설정으로 어떤 걸 쓸지 결정해요
@Slf4j
@Service
@RequiredArgsConstructor
@org.springframework.context.annotation.Primary
public class GeminiAiService implements AiService {

    // application.properties에서 값을 자동으로 읽어와요
    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.gemini.url}")
    private String apiUrl;

    // RestTemplate = 외부 API를 호출할 때 쓰는 도구예요
    // JpaConfig에서 @Bean으로 등록해뒀어요
    private final RestTemplate restTemplate;

    // Jackson = JSON을 Java 객체로, Java 객체를 JSON으로 변환해주는 라이브러리
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String chat(List<Message> messages) {
        try {
            // ① Gemini API가 요구하는 형식으로 요청 데이터 만들기
            // Gemini는 이런 형식으로 요청을 받아요:
            // { "contents": [ { "role": "user", "parts": [{"text": "안녕"}] } ] }
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contents = objectMapper.createArrayNode();

            for (Message message : messages) {
                ObjectNode content = objectMapper.createObjectNode();
                // Gemini는 "assistant" 대신 "model"을 써요
                content.put("role", message.role().equals("assistant") ? "model" : message.role());

                ArrayNode parts = objectMapper.createArrayNode();
                ObjectNode part = objectMapper.createObjectNode();
                part.put("text", message.content());
                parts.add(part);
                content.set("parts", parts);
                contents.add(content);
            }

            requestBody.set("contents", contents);

            // ② HTTP 헤더 설정
            // Content-Type: application/json = "JSON 형식으로 보낼게요"
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(
                    objectMapper.writeValueAsString(requestBody), headers);

            // ③ Gemini API 호출
            // URL 뒤에 ?key={apiKey} 붙여서 인증해요
            String url = apiUrl + "?key=" + apiKey;
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // ④ 응답에서 텍스트만 꺼내기
            // Gemini 응답 구조:
            // { "candidates": [ { "content": { "parts": [ { "text": "AI 응답" } ] } } ] }
            JsonNode root = objectMapper.readTree(response.getBody());
            String result = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();

            log.info("[Gemini] 응답 성공, 길이={}", result.length());
            return result;

        } catch (Exception e) {
            log.error("[Gemini] API 호출 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.AI_API_ERROR);
        }
    }
}
