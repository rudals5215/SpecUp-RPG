package com.specuprpg.global.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Service
@Primary
public class GeminiAiService implements AiService {

    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.gemini.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String chat(List<Message> messages) {
        try {
            // ① 요청 Body 만들기
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contents = objectMapper.createArrayNode();

            for (Message message : messages) {
                ObjectNode content = objectMapper.createObjectNode();
                content.put("role", message.role().equals("assistant") ? "model" : message.role());

                ArrayNode parts = objectMapper.createArrayNode();
                ObjectNode part = objectMapper.createObjectNode();
                part.put("text", message.content());
                parts.add(part);
                content.set("parts", parts);
                contents.add(content);
            }

            requestBody.set("contents", contents);
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            log.info("[Gemini] 요청 시작");
            log.info("[Gemini] apiKey 앞 10자: {}", apiKey.substring(0, 10));
            log.info("[Gemini] apiUrl: {}", apiUrl);
            log.info("[Gemini] requestBody: {}", requestBodyJson);

            // ② URL 파라미터 방식으로 API 키 전달
            String fullUrl = apiUrl + "?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl)) // 👈 확실하게 쿼리 스트링(?key=)이 포함된 fullUrl을 넣어줍니다!
                    .header("Content-Type", "application/json") // 👈 헤더는 딱 이거 하나면 충분합니다.
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            log.info("[Gemini] 응답 상태코드: {}", response.statusCode());
            log.info("[Gemini] 응답 Body: {}", response.body());

            if (response.statusCode() != 200) {
                log.error("[Gemini] 실패 응답: {}", response.body());
                throw new CustomException(ErrorCode.AI_API_ERROR);
            }

            // ③ 응답에서 텍스트 추출
            JsonNode root = objectMapper.readTree(response.body());
            String result = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();

            log.info("[Gemini] 응답 성공, 길이={}", result.length());
            return result;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            // 상세 에러 로그
            log.error("[Gemini] API 호출 실패 상세: {} - {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace();
            throw new CustomException(ErrorCode.AI_API_ERROR);
        }
    }
}