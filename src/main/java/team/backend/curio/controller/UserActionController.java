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
    public ResponseEntity<Void> likeNews(@PathVariable("newsId") Long newsId,
                                         @RequestParam Long userId){
        userActionService.likeNews(userId, newsId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}/like")
    public ResponseEntity<Void> unlikeNews(@PathVariable("newsId") Long newsId,
                                           @RequestParam Long userId){
        userActionService.unlikeNews(userId, newsId);
        return ResponseEntity.ok().build();
    }
}
