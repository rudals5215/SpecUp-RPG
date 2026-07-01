package com.specuprpg.domain.item.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// user_item 테이블 = 유저가 구매한 아이템 목록
// 구매한 아이템 중 is_equipped = true인 것만 캐릭터에 장착돼요
@Entity
@Table(
    name = "user_item",
    uniqueConstraints = {
        // 같은 아이템을 두 번 살 수 없어요
        @UniqueConstraint(columnNames = {"user_id", "item_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // 현재 장착 중인지 여부
    @Column(nullable = false)
    private boolean isEquipped;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime acquiredAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static UserItem create(User user, Item item) {
        UserItem userItem = new UserItem();
        userItem.user = user;
        userItem.item = item;
        userItem.isEquipped = false; // 처음 구매 시 장착 안 된 상태
        return userItem;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────

    // 아이템 장착
    public void equip() {
        this.isEquipped = true;
    }

    // 아이템 해제
    public void unequip() {
        this.isEquipped = false;
    }
}
