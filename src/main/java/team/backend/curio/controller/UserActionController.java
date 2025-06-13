package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.dto.ActionGetResponse;
import team.backend.curio.dto.ActionPatchResponse;
import team.backend.curio.dto.CommonResponseDto;
import team.backend.curio.security.CustomUserDetails;
import team.backend.curio.service.UserActionService;

import java.util.Map;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor

public class UserActionController {

    private final UserActionService userActionService;

    // 좋아요 여부 조회
    @Operation(summary = "기사 좋아요 여부 조회")
    @GetMapping("/{articleId}/like")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        boolean liked = userActionService.isLiked(userDetails.getUserId(), articleId);
        return ResponseEntity.ok(new ActionGetResponse(articleId, liked));
    }

    // 좋아요 등록 및 취소
    @Operation(summary = "기사 좋아요 등록/취소")
    @PatchMapping("/{articleId}/like")
    public ResponseEntity<?> likeNews(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        int vote = userActionService.likeNews(userDetails.getUserId(), articleId);
        String message = (vote == 1) ? "이 기사를 좋아합니다" : "좋아요를 취소했습니다.";

        return ResponseEntity.ok(new ActionPatchResponse(message, vote == 1));
    }

    // 추천 여부 조회
    @Operation(summary = "기사 추천 여부 조회")
    @GetMapping("/{articleId}/recommend")
    public ResponseEntity<?> getRecommendStatus(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        int vote = userActionService.getVote(userDetails.getUserId(), articleId);
        boolean recommended = (vote == 1);  // 추천이면 true, 아니면 false
        return ResponseEntity.ok(new ActionGetResponse(articleId, recommended));
    }

    // 추천 등록 및 취소
    @Operation(summary = "기사 추천 등록/취소")
    @PatchMapping("/{articleId}/recommend")
    public ResponseEntity<?> recommend(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        int vote = userActionService.recommendNews(userDetails.getUserId(), articleId);
        String message = (vote == 1) ? "이 기사를 추천합니다" : "추천을 취소했습니다";

        return ResponseEntity.ok(new ActionPatchResponse(message, vote == 1));
    }

    // 비추천 여부 조회
    @Operation(summary = "기사 비추천 여부 조회")
    @GetMapping("/{articleId}/notrecommend")
    public ResponseEntity<?> getNotRecommendStatus(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        int vote = userActionService.getVote(userDetails.getUserId(), articleId);
        boolean notRecommended = (vote == -1);  // 추천이면 true, 아니면 false
        return ResponseEntity.ok(new ActionGetResponse(articleId, notRecommended));
    }

    // 비추천 등록 및 취소
    @Operation(summary = "기사 비추천 등록/취소")
    @PatchMapping("/{articleId}/notrecommend")
    public ResponseEntity<?> notRecommend(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        int vote = userActionService.notRecommendNews(userDetails.getUserId(), articleId);
        String message = (vote == -1) ? "이 기사를 비추천합니다" : "비추천을 취소했습니다";

        return ResponseEntity.ok(new ActionPatchResponse(message, vote == -1));
    }
}
