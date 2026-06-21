package com.specuprpg.domain.quest.repository;

import com.specuprpg.domain.quest.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// quest 테이블 창고지기
// 메뉴판에서 퀘스트 목록을 꺼내오는 역할이에요
public interface QuestRepository extends JpaRepository<Quest, Long> {

    // 카테고리 + 퀘스트 타입으로 템플릿 퀘스트 조회
    // 예: DEVELOPER 카테고리의 DAILY 퀘스트 목록 가져오기
    List<Quest> findByCategoryAndQuestTypeAndIsTemplate(
            String category, String questType, boolean isTemplate);
}
