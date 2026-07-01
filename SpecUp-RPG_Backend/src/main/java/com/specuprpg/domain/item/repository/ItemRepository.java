package com.specuprpg.domain.item.repository;

import com.specuprpg.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // 아이템 타입으로 조회 (상점 필터링)
    List<Item> findByItemType(String itemType);

    // 직업별 아이템 조회 (null = 공용 아이템)
    List<Item> findByJobTypeOrJobTypeIsNull(String jobType);

    // 아이템 타입 + 직업별 조회
    List<Item> findByItemTypeAndJobTypeOrItemTypeAndJobTypeIsNull(
            String itemType1, String jobType, String itemType2);
}
