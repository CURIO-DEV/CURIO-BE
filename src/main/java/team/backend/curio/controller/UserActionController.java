package team.backend.curio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.service.UserActionService;

@RestController
@RequestMapping("/api/articles")
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

    @PostMapping("/{articleId}/recommend")
    public ResponseEntity<Void> recommend(@PathVariable Long articleId,
                                          @RequestParam Long userId) {
        userActionService.recommendNews(userId, articleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}/recommend")
    public ResponseEntity<Void> cancelrecommend(@PathVariable Long articleId,
                                                @RequestParam Long userId) {
        userActionService.cancelRecommend(userId, articleId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{articleId}/notrecommend")
    public ResponseEntity<Void> notRecommend(@PathVariable long articleId,
                                             @RequestParam long userId) {
        userActionService.notRecommendNews(userId, articleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}/notrecommend")
    public ResponseEntity<Void> cancelNotRecommend(@PathVariable Long articleId,
                                                   @RequestParam Long userId){
        userActionService.cancelNotRecommend(userId, articleId);
        return ResponseEntity.ok().build();
    }

}
