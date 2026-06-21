package com.specuprpg.global.ai;

import java.util.List;

// AI 서비스 인터페이스 = 설계도
// Gemini든 Claude든 이 약속을 지켜야 해요
// 나중에 AI 제공사를 바꿔도 이 파일은 건드릴 필요 없어요
public interface AiService {

    // AI에게 메시지를 보내고 응답을 받는 핵심 메서드
    // messages = 지금까지 나눈 대화 목록 (컨텍스트 유지용)
    String chat(List<Message> messages);

    // 대화 메시지 구조
    // role = "user" (유저가 한 말) 또는 "model" (AI가 한 말)
    // content = 실제 메시지 내용
    record Message(String role, String content) {}
}
