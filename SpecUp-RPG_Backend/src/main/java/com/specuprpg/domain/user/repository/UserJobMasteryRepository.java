package com.specuprpg.domain.user.repository;

import com.specuprpg.domain.user.entity.UserJobMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJobMasteryRepository extends JpaRepository<UserJobMastery, Long> {

    // 유저의 모든 직업 숙련도 조회
    List<UserJobMastery> findAllByUserId(Long userId);

    // 특정 직업 숙련도 조회
    Optional<UserJobMastery> findByUserIdAndJobType(Long userId, String jobType);

    // 특정 직업 존재 여부
    boolean existsByUserIdAndJobType(Long userId, String jobType);
}
