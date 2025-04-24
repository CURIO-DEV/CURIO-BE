package team.backend.curio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.service.UserActionService;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor


public class UserActionController {

    private final UserActionService userActionService;

    // 좋아요 등록
    @PostMapping("/{articleId}/like")
    public ResponseEntity<Void> likeNews(@PathVariable Long articleId,
                                         @RequestParam Long userId) {
        userActionService.likeNews(userId, articleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}/like")
    public ResponseEntity<Void> unlikeNews(@PathVariable Long articleId,
                                           @RequestParam Long userId) {
        userActionService.unlikeNews(userId, articleId);
        return ResponseEntity.ok().build();
    }

    // 추천 등록
    @PostMapping("/{articleId}/recommend")
    public ResponseEntity<Void> recommend(@PathVariable Long articleId,
                                          @RequestParam Long userId) {
        userActionService.recommendNews(userId, articleId);
        userActionService.cancelNotRecommend(userId, articleId); // 추천하면 비추천 취소
        return ResponseEntity.ok().build();
    }

    // 추천 취소
    @DeleteMapping("/{articleId}/recommend")
    public ResponseEntity<Void> cancelrecommend(@PathVariable Long articleId,
                                                @RequestParam Long userId) {
        userActionService.cancelRecommend(userId, articleId);
        return ResponseEntity.ok().build();
    }

    // 비추천 등록
    @PostMapping("/{articleId}/notrecommend")
    public ResponseEntity<Void> notRecommend(@PathVariable long articleId,
                                             @RequestParam long userId) {
        userActionService.notRecommendNews(userId, articleId);
        userActionService.cancelRecommend(userId, articleId); // 비추천하면 추천 취소
        return ResponseEntity.ok().build();
    }

    // 비추천 취소
    @DeleteMapping("/{articleId}/notrecommend")
    public ResponseEntity<Void> cancelNotRecommend(@PathVariable Long articleId,
                                                   @RequestParam Long userId){
        userActionService.cancelNotRecommend(userId, articleId);
        return ResponseEntity.ok().build();
    }

}
