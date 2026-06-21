package com.specuprpg.domain.ai.service;

import com.specuprpg.domain.ai.dto.AiRequestDto;
import com.specuprpg.domain.ai.dto.AiResponseDto;
import com.specuprpg.domain.ai.entity.AiCurriculum;
import com.specuprpg.domain.ai.repository.AiCurriculumRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiCurriculumService {

    private final AiCurriculumRepository aiCurriculumRepository;

    // ── 현재 커리큘럼 조회 ────────────────────────────────
    @Transactional(readOnly = true)
    public AiResponseDto.CurriculumDetail getCurriculum(Long userId) {

        List<AiCurriculum> curriculums =
                aiCurriculumRepository.findByUserIdAndIsActiveTrueOrderByPriorityAsc(userId);

        if (curriculums.isEmpty()) {
            // 커리큘럼 없으면 빈 응답 반환
            return AiResponseDto.CurriculumDetail.builder()
                    .activeCurriculum(null)
                    .pausedCurriculums(List.of())
                    .build();
        }

        // 우선순위 1번 = 현재 진행 중인 커리큘럼
        AiCurriculum active = curriculums.get(0);

        // 나머지 = 일시정지된 커리큘럼
        List<AiResponseDto.CurriculumDetail.PausedCurriculum> paused = curriculums.stream()
                .skip(1)
                .filter(c -> c.getPausedAt() != null)
                .map(AiResponseDto.CurriculumDetail.PausedCurriculum::from)
                .toList();

        return AiResponseDto.CurriculumDetail.builder()
                .activeCurriculum(AiResponseDto.CurriculumDetail.ActiveCurriculum.from(active))
                .pausedCurriculums(paused)
                .build();
    }

    // ── 목표 변경 처리 ────────────────────────────────────
    @Transactional
    public AiResponseDto.CurriculumChangeResult changeCurriculum(
            Long userId, AiRequestDto.CurriculumChange request) {

        return switch (request.getChangeType()) {
            case "PAUSE_AND_ADD" -> handlePauseAndAdd(userId, request);
            case "SWITCH" -> handleSwitch(userId);
            default -> throw new CustomException(ErrorCode.AI_CURRICULUM_NOT_FOUND);
        };
    }

    // 단기 목표 추가 — 기존 커리큘럼 일시정지 후 새 커리큘럼 진행
    private AiResponseDto.CurriculumChangeResult handlePauseAndAdd(
            Long userId, AiRequestDto.CurriculumChange request) {

        // 현재 메인 커리큘럼 찾기
        AiCurriculum current = aiCurriculumRepository
                .findTopByUserIdAndIsActiveTrueOrderByPriorityAsc(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.AI_CURRICULUM_NOT_FOUND));

        // 기존 커리큘럼 일시정지
        current.pause(request.getResumeAt());

        log.info("[커리큘럼 변경] userId={}, type=PAUSE_AND_ADD, resumeAt={}",
                userId, request.getResumeAt());

        return AiResponseDto.CurriculumChangeResult.builder()
                .pausedCurriculum(AiResponseDto.CurriculumChangeResult.PausedInfo.builder()
                        .curriculumId(current.getId())
                        .title(current.getTitle())
                        .resumeAt(current.getResumeAt())
                        .build())
                .message(request.getResumeAt() + "에 자동으로 원래 커리큘럼으로 복귀돼요!")
                .build();
    }

    // 목표 완전 전환 — 기존 커리큘럼 비활성화
    private AiResponseDto.CurriculumChangeResult handleSwitch(Long userId) {

        // 현재 활성화된 커리큘럼 전부 비활성화
        List<AiCurriculum> curriculums =
                aiCurriculumRepository.findByUserIdAndIsActiveTrueOrderByPriorityAsc(userId);
        curriculums.forEach(AiCurriculum::deactivate);

        log.info("[커리큘럼 변경] userId={}, type=SWITCH, 비활성화={}개", userId, curriculums.size());

        return AiResponseDto.CurriculumChangeResult.builder()
                .message("기존 커리큘럼을 종료했어요. AI 진단을 다시 시작해서 새 커리큘럼을 만들어봐요!")
                .build();
    }
}
