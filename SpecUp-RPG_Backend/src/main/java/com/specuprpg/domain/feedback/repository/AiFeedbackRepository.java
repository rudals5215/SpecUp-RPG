package com.specuprpg.domain.feedback.repository;

import com.specuprpg.domain.feedback.entity.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {

    // 유저의 피드백 전체 조회 (최신순)
    List<AiFeedback> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 피드백 타입으로 조회
    List<AiFeedback> findByUserIdAndFeedbackTypeOrderByCreatedAtDesc(
            Long userId, String feedbackType);

    // 가장 최신 피드백 조회
    Optional<AiFeedback> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
