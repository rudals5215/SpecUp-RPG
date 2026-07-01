package com.specuprpg.domain.achievement.repository;

import com.specuprpg.domain.achievement.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // 조건 타입 + 수치로 칭호 조회
    // 퀘스트 완료, 레벨업 등 이벤트 발생 시 해당 조건의 칭호를 찾아요
    List<Achievement> findByConditionTypeAndConditionValue(
            String conditionType, int conditionValue);

    // 조건 타입 + 수치 이하의 칭호 조회
    // 예: 스트릭 7일 달성 시 1일, 3일, 7일 칭호 모두 체크
    List<Achievement> findByConditionTypeAndConditionValueLessThanEqual(
            String conditionType, int conditionValue);
}
