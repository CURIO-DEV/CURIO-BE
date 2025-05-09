package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.dto.CommonResponseDto;
import team.backend.curio.service.UserActionService;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor

public class UserActionController {

    private final UserActionService userActionService;

    // 좋아요 등록
    @Operation(summary = "기사 좋아요 등록")
    @PostMapping("/{articleId}/like")
    public ResponseEntity<CommonResponseDto<Void>> likeNews(@PathVariable Long articleId, @RequestParam Long userId) {
        userActionService.likeNews(userId, articleId);
        return ResponseEntity.ok(new CommonResponseDto<>(true,"이 기사를 좋아합니다",null));
    }

    @Operation(summary = "기사 좋아요 취소")
    @DeleteMapping("/{articleId}/like")
    public ResponseEntity<CommonResponseDto<Void>> unlikeNews(@PathVariable Long articleId, @RequestParam Long userId) {
        userActionService.unlikeNews(userId, articleId);
        return ResponseEntity.ok(new CommonResponseDto<>(true,"좋아요를 취소했습니다",null));
    }

    // 추천 등록
    @Operation(summary = "기사 추천 등록")
    @PostMapping("/{articleId}/recommend")
    public ResponseEntity<CommonResponseDto<Void>> recommend(@PathVariable Long articleId, @RequestParam Long userId) {
        userActionService.recommendNews(userId, articleId);
        userActionService.cancelNotRecommend(userId, articleId); // 추천하면 비추천 취소
        return ResponseEntity.ok(new CommonResponseDto<>(true,"이 기사를 추천합니다",null));
    }

    // 추천 취소
    @Operation(summary = "기사 추천 취소")
    @DeleteMapping("/{articleId}/recommend")
    public ResponseEntity<CommonResponseDto<Void>> cancelrecommend(@PathVariable Long articleId, @RequestParam Long userId) {
        userActionService.cancelRecommend(userId, articleId);
        return ResponseEntity.ok(new CommonResponseDto<>(true,"추천을 취소했습니다",null));
    }

    // 비추천 등록
    @Operation(summary = "기사 비추천 등록")
    @PostMapping("/{articleId}/notrecommend")
    public ResponseEntity<CommonResponseDto<Void>> notRecommend(@PathVariable long articleId, @RequestParam long userId) {
        userActionService.notRecommendNews(userId, articleId);
        userActionService.cancelRecommend(userId, articleId); // 비추천하면 추천 취소
        return ResponseEntity.ok(new CommonResponseDto<>(true,"이 기사를 비추천합니다",null));
    }

    // 비추천 취소
    @Operation(summary = "기사 비추천 취소")
    @DeleteMapping("/{articleId}/notrecommend")
    public ResponseEntity<CommonResponseDto<Void>> cancelNotRecommend(@PathVariable Long articleId, @RequestParam Long userId){
        userActionService.cancelNotRecommend(userId, articleId);
        return ResponseEntity.ok(new CommonResponseDto<>(true,"비추천을 취소했습니다",null));
    }
}
