package com.specuprpg.global.response;

import lombok.Getter;

// 모든 API 응답을 이 형식으로 통일해요
// { "success": true, "data": { }, "message": "..." }
// 프론트에서 response.data.success 로 성공 여부를 항상 같은 방식으로 확인할 수 있어요
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;

    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // ── 성공 응답 ─────────────────────────────────────────

    // 데이터 + 메시지 둘 다 있을 때
    // 예: ApiResponse.success(userDto, "로그인 성공!")
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    // 데이터만 있을 때 (메시지 기본값)
    // 예: ApiResponse.success(userDto)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "요청이 성공했습니다.");
    }

    // 데이터 없이 메시지만 있을 때
    // 예: ApiResponse.successMessage("로그아웃됐어요.")
    public static <Void> ApiResponse<Void> successMessage(String message) {
        return new ApiResponse<>(true, null, message);
    }

    // ── 실패 응답 ─────────────────────────────────────────

    // 예: ApiResponse.fail("오늘 이미 완료한 퀘스트예요.")
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}