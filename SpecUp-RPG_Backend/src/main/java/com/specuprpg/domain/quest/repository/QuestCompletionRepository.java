package com.specuprpg.domain.quest.repository;

import com.specuprpg.domain.quest.entity.QuestCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// quest_completion 테이블 창고지기
// 퀘스트 완료 이력을 관리해요
// 중복 완료 방지에 핵심적인 역할을 해요
public interface QuestCompletionRepository extends JpaRepository<QuestCompletion, Long> {

    // 오늘 이미 완료했는지 체크 (중복 완료 방지 2차 방어)
    // 1차 방어는 DB UNIQUE 제약, 2차 방어는 이 코드로 미리 막는 거예요
    boolean existsByUserQuestIdAndCompletedDate(Long userQuestId, LocalDate completedDate);

    // 오늘 완료한 퀘스트 개수 조회 (대시보드, 통계용)
    @Query("""
        SELECT COUNT(qc) FROM QuestCompletion qc
        WHERE qc.user.id = :userId
        AND qc.completedDate = :date
    """)
    int countByUserIdAndCompletedDate(@Param("userId") Long userId,
                                      @Param("date") LocalDate date);

    // 기간별 완료 이력 조회 (주간 통계용)
    @Query("""
        SELECT qc FROM QuestCompletion qc
        WHERE qc.user.id = :userId
        AND qc.completedDate BETWEEN :startDate AND :endDate
        ORDER BY qc.completedDate ASC
    """)
    List<QuestCompletion> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
