package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.dto.NewsDTO.CurioGoNewsResponseDto;
import team.backend.curio.service.NewsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CurioGoController {
    private final NewsService newsService;


    // [CurioGo] 사용자의 관심 카테고리를 기반으로 인기 뉴스 5개 조회
    @Operation(summary = "큑 보러가기")
    @GetMapping("/curio-go")
    public ResponseEntity<List<CurioGoNewsResponseDto>> getCurioGoNews(@RequestParam Long userId) {
        List<CurioGoNewsResponseDto> newsList = newsService.getCurioGoNewsByUserId(userId);
        return ResponseEntity.ok(newsList);
    }
}
