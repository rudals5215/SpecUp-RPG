package com.specuprpg.domain.item.repository;

import com.specuprpg.domain.item.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    // 유저의 보유 아이템 전체 조회
    @Query("SELECT ui FROM UserItem ui JOIN FETCH ui.item WHERE ui.user.id = :userId")
    List<UserItem> findAllByUserId(@Param("userId") Long userId);

    // 특정 아이템 보유 여부 확인 (중복 구매 방지)
    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    // 특정 아이템 조회
    Optional<UserItem> findByUserIdAndItemId(Long userId, Long itemId);

    // 장착 중인 아이템만 조회
    @Query("SELECT ui FROM UserItem ui JOIN FETCH ui.item WHERE ui.user.id = :userId AND ui.isEquipped = true")
    List<UserItem> findEquippedByUserId(@Param("userId") Long userId);
}
