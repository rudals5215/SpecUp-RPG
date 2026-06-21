package com.specuprpg.domain.ai.repository;

import com.specuprpg.domain.ai.entity.AiCurriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AiCurriculumRepository extends JpaRepository<AiCurriculum, Long> {

    // 현재 활성화된 커리큘럼 조회 (우선순위 순)
    List<AiCurriculum> findByUserIdAndIsActiveTrueOrderByPriorityAsc(Long userId);

    // 메인 커리큘럼 조회 (우선순위 1번)
    Optional<AiCurriculum> findTopByUserIdAndIsActiveTrueOrderByPriorityAsc(Long userId);

    // 자동 복귀 대상 커리큘럼 조회 (Scheduler용)
    // resumeAt이 오늘 이하인 일시정지 커리큘럼
    @Query("""
        SELECT c FROM AiCurriculum c
        WHERE c.isActive = true
        AND c.pausedAt IS NOT NULL
        AND c.resumeAt <= :today
    """)
    List<AiCurriculum> findCurriculumsToResume(@Param("today") LocalDate today);
}
