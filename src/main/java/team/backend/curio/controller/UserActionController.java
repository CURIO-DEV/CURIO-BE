package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.dto.CommonResponseDto;
import team.backend.curio.security.CustomUserDetails;
import team.backend.curio.service.UserActionService;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor

public class UserActionController {

    private final UserActionService userActionService;

    // 좋아요 등록 및 취소
    @Operation(summary = "기사 좋아요 등록/취소")
    @PatchMapping("/{articleId}/like")
    public ResponseEntity<CommonResponseDto<String>> likeNews(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        int vote = userActionService.likeNews(userDetails.getUserId(), articleId);
        String message = (vote == 1) ? "이 기사를 좋아합니다" : "좋아요를 취소했습니다.";
        String status = (vote == 1) ? "좋아요" : "좋아요 없음";

        return ResponseEntity.ok(new CommonResponseDto<>(true, message, status));
    }

    // 추천 등록 및 취소
    @Operation(summary = "기사 추천 등록/취소")
    @PatchMapping("/{articleId}/recommend")
    public ResponseEntity<CommonResponseDto<String>> recommend(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        int vote = userActionService.recommendNews(userDetails.getUserId(), articleId);
        String message = (vote == 1) ? "이 기사를 추천합니다" : "추천을 취소했습니다";
        String status = (vote == 1) ? "추천" : "추천 없음";

        return ResponseEntity.ok(new CommonResponseDto<>(true, message, status));
    }

    // 비추천 등록 및 취소
    @Operation(summary = "기사 비추천 등록/취소")
    @PatchMapping("/{articleId}/notrecommend")
    public ResponseEntity<CommonResponseDto<String>> notRecommend(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        int vote = userActionService.notRecommendNews(userDetails.getUserId(), articleId);
        String message = (vote == -1) ? "이 기사를 비추천합니다" : "비추천을 취소했습니다";
        String status = (vote == -1) ? "비추천" : "비추천 없음";

        return ResponseEntity.ok(new CommonResponseDto<>(true, message, status));
    }
}
