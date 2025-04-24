package team.backend.curio.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.domain.News;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.dto.NewsDTO.RelatedNewsResponse;
import team.backend.curio.service.NewsService;

import java.util.List;

@RestController
@RequestMapping("/articles")  // api/articles로 경로 설정
@RequiredArgsConstructor

public class ArticleController {
    private final NewsService newsService;  // NewsService 주입

    // 특정 뉴스의 관련 뉴스 조회
    @GetMapping("/{articleId}/related")
    public ResponseEntity<List<RelatedNewsResponse>> getRelatedNews(@PathVariable Long articleId) {
        List<RelatedNewsResponse> relatedNews = newsService.getRelatedNews(articleId);
        return ResponseEntity.ok(relatedNews);
    }

    // 특정 뉴스의 헤드라인과 이미지 URL 조회
    @GetMapping("/{articleId}/headline")
    public ResponseEntity<NewsResponseDto> getArticleHeadline(@PathVariable Long articleId) {
        NewsResponseDto newsResponse = newsService.getArticleHeadline(articleId); // NewsService에서 데이터 가져오기
        return ResponseEntity.ok(newsResponse); // 헤드라인과 이미지 URL 반환
    }

    // 뉴스 저장 API
    @Operation(summary = "크롤링 뉴스 데이터 저장")
    @PostMapping("/crawler")
    public ResponseEntity<String> saveNews(@RequestBody List<News> newsList) {
        System.out.println("API 호출됨!");
        newsService.saveAllNews(newsList);
        return ResponseEntity.ok("크롤링 뉴스 저장 성공!");
    }

    @Operation(summary = "News table 데이터 불러오기")
    @GetMapping("/list")
    public List<News> getNewsList() {
        return newsService.getAllNews();
    }
}
