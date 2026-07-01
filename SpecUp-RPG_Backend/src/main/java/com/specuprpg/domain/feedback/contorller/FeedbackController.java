package com.specuprpg.domain.feedback.controller;

import com.specuprpg.domain.feedback.dto.FeedbackResponseDto;
import com.specuprpg.domain.feedback.service.FeedbackService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final JwtProvider jwtProvider;

    // GET /api/feedbacks?type=DAILY
    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto.FeedbackInfo>>> getFeedbacks(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String type) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(
                feedbackService.getFeedbacks(userId, type)));
    }

    // GET /api/feedbacks/latest
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<FeedbackResponseDto.LatestFeedback>> getLatestFeedback(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(
                feedbackService.getLatestFeedback(userId)));
    }

    private Long extractUserId(String bearerToken) {
        return jwtProvider.getUserId(bearerToken.substring(7));
    }
}
