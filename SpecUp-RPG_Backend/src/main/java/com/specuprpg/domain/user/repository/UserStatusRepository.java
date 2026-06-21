package com.specuprpg.domain.user.repository;

import com.specuprpg.domain.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

    // 유저 ID로 스탯 조회
    Optional<UserStatus> findByUserId(Long userId);
}
