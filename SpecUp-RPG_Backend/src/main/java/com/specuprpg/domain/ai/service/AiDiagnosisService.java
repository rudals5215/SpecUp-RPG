package com.specuprpg.domain.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.specuprpg.domain.ai.dto.AiRequestDto;
import com.specuprpg.domain.ai.dto.AiResponseDto;
import com.specuprpg.domain.ai.entity.AiCurriculum;
import com.specuprpg.domain.ai.entity.AiDiagnosis;
import com.specuprpg.domain.ai.repository.AiCurriculumRepository;
import com.specuprpg.domain.ai.repository.AiDiagnosisRepository;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.repository.UserRepository;
import com.specuprpg.global.ai.AiService;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiDiagnosisService {

    private final AiService aiService;  // Gemini 또는 Claude (인터페이스로 주입)
    private final AiDiagnosisRepository aiDiagnosisRepository;
    private final AiCurriculumRepository aiCurriculumRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // AI 진단 시스템 프롬프트
    // Gemini한테 "너는 이런 역할이야"라고 알려주는 설명서예요
    private static final String SYSTEM_PROMPT = """
            당신은 SpecUp RPG의 AI 성장 코치입니다.
            유저와 대화를 통해 현재 실력, 목표, 하루 가용 시간을 파악하고
            맞춤형 학습 커리큘럼을 만들어주세요.
            
            대화 규칙:
            1. 친근하고 편안한 말투를 사용하세요.
            2. 한 번에 하나의 질문만 하세요.
            3. 실력(현재 수준), 목표(무엇을 이루고 싶은지), 시간(하루 몇 시간) 파악 후
               진단을 완료하세요.
            4. 진단이 완료되면 반드시 아래 JSON 형식으로 응답하세요:
            
            ###DIAGNOSIS_COMPLETE###
            {
              "assessedLevel": "BEGINNER|INTERMEDIATE|ADVANCED",
              "goalSummary": "목표 요약",
              "dailyHours": 2.0,
              "curriculumTitle": "커리큘럼 제목",
              "totalWeeks": 4,
              "roadmap": [
                {"week": 1, "title": "1주차 제목", "quests": ["퀘스트1", "퀘스트2"]},
                {"week": 2, "title": "2주차 제목", "quests": ["퀘스트1", "퀘스트2"]}
              ]
            }
            """;

    // ── AI 진단 대화 처리 ─────────────────────────────────
    @Transactional
    public AiResponseDto.DiagnosisChatResult chat(Long userId, AiRequestDto.DiagnosisChat request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // ① AI 토큰 차감 (1개)
        // 토큰이 부족하면 false 반환 → 예외 처리
        if (!user.useAiToken(1)) {
            throw new CustomException(ErrorCode.AI_TOKEN_INSUFFICIENT);
        }

        // ② 대화 목록 구성
        // 시스템 프롬프트 + 이전 대화 + 새 메시지 순서로 담아요
        List<AiService.Message> messages = new ArrayList<>();

        // 시스템 프롬프트는 항상 첫 번째로
        messages.add(new AiService.Message("user", SYSTEM_PROMPT));
        messages.add(new AiService.Message("model", "알겠어요! 성장 코치로서 도와드릴게요. 시작해볼까요?"));

        // 이전 대화 이력 추가
        if (request.getConversationHistory() != null) {
            for (AiRequestDto.DiagnosisChat.ChatMessage msg : request.getConversationHistory()) {
                messages.add(new AiService.Message(msg.getRole(), msg.getContent()));
            }
        }

        // 새 메시지 추가
        messages.add(new AiService.Message("user", request.getMessage()));

        // ③ Gemini API 호출
        String aiReply = aiService.chat(messages);
        log.info("[AI 진단] userId={}, reply 길이={}", userId, aiReply.length());

        // ④ 진단 완료 여부 확인
        // AI 응답에 ###DIAGNOSIS_COMPLETE### 가 있으면 진단 완료
        if (aiReply.contains("###DIAGNOSIS_COMPLETE###")) {
            return handleDiagnosisComplete(user, aiReply);
        }

        // 아직 대화 중이면 응답만 반환
        return AiResponseDto.DiagnosisChatResult.builder()
                .reply(aiReply)
                .isDiagnosisComplete(false)
                .remainingAiToken(user.getAiToken())
                .build();
    }

    // ── 진단 완료 처리 ────────────────────────────────────
    private AiResponseDto.DiagnosisChatResult handleDiagnosisComplete(User user, String aiReply) {
        try {
            // ###DIAGNOSIS_COMPLETE### 이후의 JSON 부분만 추출
            String jsonPart = aiReply.substring(
                    aiReply.indexOf("###DIAGNOSIS_COMPLETE###") + "###DIAGNOSIS_COMPLETE###".length()
            ).trim();

            // JSON에서 { } 부분만 정확히 추출
            int jsonStart = jsonPart.indexOf("{");
            int jsonEnd = jsonPart.lastIndexOf("}") + 1;
            String json = jsonPart.substring(jsonStart, jsonEnd);

            JsonNode diagnosisData = objectMapper.readTree(json);

            // DB에 진단 결과 저장
            AiDiagnosis diagnosis = AiDiagnosis.create(
                    user,
                    diagnosisData.path("assessedLevel").asText(),
                    diagnosisData.path("goalSummary").asText(),
                    new BigDecimal(diagnosisData.path("dailyHours").asText()),
                    aiReply // 전체 대화 내용 저장
            );
            aiDiagnosisRepository.save(diagnosis);

            // 커리큘럼 저장
            AiCurriculum curriculum = AiCurriculum.create(
                    user,
                    diagnosis,
                    diagnosisData.path("curriculumTitle").asText(),
                    diagnosisData.path("totalWeeks").asInt(),
                    diagnosisData.path("roadmap").toString() // 로드맵 JSON 저장
            );
            aiCurriculumRepository.save(curriculum);

            log.info("[AI 진단 완료] userId={}, level={}, weeks={}",
                    user.getId(),
                    diagnosisData.path("assessedLevel").asText(),
                    diagnosisData.path("totalWeeks").asInt());

            return AiResponseDto.DiagnosisChatResult.builder()
                    .reply("진단이 완료됐어요! " + diagnosisData.path("totalWeeks").asInt()
                            + "주 커리큘럼을 만들었어요.")
                    .isDiagnosisComplete(true)
                    .diagnosis(AiResponseDto.DiagnosisChatResult.DiagnosisInfo.builder()
                            .diagnosisId(diagnosis.getId())
                            .assessedLevel(diagnosis.getAssessedLevel())
                            .goalSummary(diagnosis.getGoalSummary())
                            .dailyHours(diagnosis.getDailyHours().doubleValue())
                            .build())
                    .curriculum(AiResponseDto.DiagnosisChatResult.CurriculumInfo.builder()
                            .curriculumId(curriculum.getId())
                            .title(curriculum.getTitle())
                            .totalWeeks(curriculum.getTotalWeeks())
                            .build())
                    .remainingAiToken(user.getAiToken())
                    .build();

        } catch (Exception e) {
            log.error("[AI 진단] JSON 파싱 실패: {}", e.getMessage());
            // JSON 파싱 실패해도 대화는 계속 진행
            return AiResponseDto.DiagnosisChatResult.builder()
                    .reply("진단 결과를 처리하는 중 문제가 생겼어요. 다시 한번 정리해서 말씀해주실 수 있을까요?")
                    .isDiagnosisComplete(false)
                    .remainingAiToken(user.getAiToken())
                    .build();
        }
    }
}
