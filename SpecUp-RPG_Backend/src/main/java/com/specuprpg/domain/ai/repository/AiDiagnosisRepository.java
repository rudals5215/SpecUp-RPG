package com.specuprpg.domain.ai.repository;

import com.specuprpg.domain.ai.entity.AiDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiDiagnosisRepository extends JpaRepository<AiDiagnosis, Long> {

    // 가장 최근 진단 결과 조회 (재진단 컨텍스트용)
    Optional<AiDiagnosis> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    // 유저의 모든 진단 이력 조회
    List<AiDiagnosis> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
