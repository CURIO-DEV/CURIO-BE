package team.backend.curio.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.domain.News;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.dto.NewsDTO.NewsWithCountsDto;
import team.backend.curio.dto.NewsDTO.NewsSummaryResponseDto;
import team.backend.curio.service.NewsService;
import team.backend.curio.service.NewsSummaryService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")  // api/articles로 경로 설정
@RequiredArgsConstructor

public class ArticleController {
    private final NewsService newsService;  // NewsService 주입
    private final NewsSummaryService processor;

    // 특정 뉴스의 관련 뉴스 조회
    @Operation(summary = "특정 뉴스의 관련 뉴스 조회 : 상세기사 화면")
    @GetMapping("/{articleId}/related")
    public ResponseEntity<List<NewsWithCountsDto>> getRelatedNews(@PathVariable Long articleId) {
        List<News> relatedNewsList = newsService.getRelatedNews(articleId);
        List<NewsWithCountsDto> dtoList = relatedNewsList.stream()
                .map(NewsWithCountsDto::new)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    // 해당 카테고리별 리스트 출력
    @GetMapping("/interests")
    public List<NewsResponseDto> getNewsByInterest(@RequestParam String category) {
        return newsService.getNewsByInterestSortedByRecent(category)
                .stream()
                .map(NewsResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 뉴스의 헤드라인과 이미지 URL 조회
    @Operation(summary = "특정 뉴스의 헤드라인과 이미지 url 조회")
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

    // 뉴스 요약 API
    @Operation(summary = "요약 정도에 따라 뉴스 요약 반환")
    @GetMapping("/{articleId}/summary")
    public ResponseEntity<?> getArticleSummary(
            @PathVariable Long articleId,
            @RequestParam String type) {

        try {
            NewsSummaryResponseDto response = newsService.getSummaryByType(articleId, type);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
  
    // 뉴스요약해서 저장
    @Operation(summary = "뉴스 요약해서 저장")
    @PostMapping("/{id}/summarize")
    public ResponseEntity<String> summarizeNews(@PathVariable Long id) {
        processor.summarizeAndSaveNews(id);
        return ResponseEntity.ok("요약 완료");
    }
}
