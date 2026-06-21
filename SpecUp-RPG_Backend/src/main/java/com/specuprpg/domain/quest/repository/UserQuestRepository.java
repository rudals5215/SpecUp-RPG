package com.specuprpg.domain.quest.repository;

import com.specuprpg.domain.quest.entity.UserQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// user_quest 테이블 창고지기
// 유저에게 할당된 퀘스트 목록을 관리해요
public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {

    // 오늘의 퀘스트 목록 조회
    // 특정 유저의 특정 날짜 퀘스트를 가져와요
    // @Query = 메서드 이름만으로 표현하기 복잡할 때 직접 쿼리를 작성해요
    @Query("""
        SELECT uq FROM UserQuest uq
        JOIN FETCH uq.quest
        WHERE uq.user.id = :userId
        AND uq.dueDate = :date
        ORDER BY uq.assignedAt ASC
    """)
    List<UserQuest> findTodayQuests(@Param("userId") Long userId,
                                    @Param("date") LocalDate date);

    // 주간 퀘스트 목록 조회
    @Query("""
        SELECT uq FROM UserQuest uq
        JOIN FETCH uq.quest
        WHERE uq.user.id = :userId
        AND uq.quest.questType = 'WEEKLY'
        AND uq.dueDate BETWEEN :weekStart AND :weekEnd
    """)
    List<UserQuest> findWeeklyQuests(@Param("userId") Long userId,
                                     @Param("weekStart") LocalDate weekStart,
                                     @Param("weekEnd") LocalDate weekEnd);

    // 만료 처리할 퀘스트 조회 (Scheduler용)
    // 마감일이 지났는데 아직 ASSIGNED 상태인 퀘스트들
    @Query("""
        SELECT uq FROM UserQuest uq
        WHERE uq.status = 'ASSIGNED'
        AND uq.dueDate < :today
    """)
    List<UserQuest> findExpiredQuests(@Param("today") LocalDate today);
}
