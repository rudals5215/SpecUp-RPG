package com.specuprpg.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ── 유저 관련 ─────────────────────────────────────────
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없어요", "USER_NOT_FOUND"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일이에요", "DUPLICATE_EMAIL"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않아요", "INVALID_PASSWORD"),

    // ── 인증/인가 관련 ────────────────────────────────────
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요해요", "UNAUTHORIZED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없어요", "FORBIDDEN"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰이에요", "INVALID_TOKEN"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰이에요. 다시 로그인해주세요", "EXPIRED_TOKEN"),

    // ── 퀘스트 관련 ───────────────────────────────────────
    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없어요", "QUEST_NOT_FOUND"),
    QUEST_ALREADY_COMPLETED(HttpStatus.CONFLICT, "오늘 이미 완료한 퀘스트예요", "QUEST_ALREADY_COMPLETED"),
    QUEST_EXPIRED(HttpStatus.BAD_REQUEST, "기간이 만료된 퀘스트예요", "QUEST_EXPIRED"),
    QUEST_NOT_MINE(HttpStatus.FORBIDDEN, "본인의 퀘스트만 수정할 수 있어요", "QUEST_NOT_MINE"),
    TEMPLATE_QUEST_CANNOT_DELETE(HttpStatus.BAD_REQUEST, "시스템 퀘스트는 삭제할 수 없어요", "TEMPLATE_QUEST_CANNOT_DELETE"),

    // ── AI 관련 ───────────────────────────────────────────
    AI_TOKEN_INSUFFICIENT(HttpStatus.TOO_MANY_REQUESTS, "AI 토큰이 부족해요. 잠시 후 다시 시도해주세요", "AI_TOKEN_INSUFFICIENT"),
    AI_DIAGNOSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "AI 진단 이력을 찾을 수 없어요", "AI_DIAGNOSIS_NOT_FOUND"),
    AI_CURRICULUM_NOT_FOUND(HttpStatus.NOT_FOUND, "커리큘럼을 찾을 수 없어요", "AI_CURRICULUM_NOT_FOUND"),
    AI_API_ERROR(HttpStatus.BAD_GATEWAY, "AI 서비스에 문제가 발생했어요. 잠시 후 다시 시도해주세요", "AI_API_ERROR"),

    // ── 펫 관련 ───────────────────────────────────────────
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "펫을 찾을 수 없어요", "PET_NOT_FOUND"),

    // ── 아이템 관련 ───────────────────────────────────────
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없어요", "ITEM_NOT_FOUND"),
    ITEM_ALREADY_OWNED(HttpStatus.CONFLICT, "이미 보유한 아이템이에요", "ITEM_ALREADY_OWNED"),
    ITEM_NOT_OWNED(HttpStatus.BAD_REQUEST, "보유하지 않은 아이템이에요", "ITEM_NOT_OWNED"),
    GOLD_INSUFFICIENT(HttpStatus.BAD_REQUEST, "골드가 부족해요. 퀘스트를 더 완료해서 골드를 모아보세요!", "GOLD_INSUFFICIENT"),

    // ── 칭호 관련 ─────────────────────────────────────────
    ACHIEVEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "칭호를 찾을 수 없어요", "ACHIEVEMENT_NOT_FOUND"),

    // ── 서버 오류 ─────────────────────────────────────────
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했어요. 잠시 후 다시 시도해주세요", "INTERNAL_SERVER_ERROR");

    private final HttpStatus status;
    private final String message;
    private final String code;
}