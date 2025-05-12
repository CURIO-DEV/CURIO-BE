package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import team.backend.curio.dto.KeywordDto;
import team.backend.curio.dto.NewsDTO.NewsWithCountsDto;
import team.backend.curio.dto.PopularKeywordDto;
import team.backend.curio.service.TrendsService;

import java.util.List;

@RestController
@RequestMapping("/trends")
public class TrendsController {

    private final TrendsService trendsService;

    @Autowired
    public TrendsController(TrendsService trendsService) {
        this.trendsService = trendsService;
    }

    // 좋아요 순 인기 뉴스 4개
    @Operation(summary = "실시간 인기글 4개 불러오기")
    @GetMapping("/popular-articles")
    public List<NewsWithCountsDto> getPopularArticles() {
        return trendsService.getPopularArticles();
    }

    // 인기 키워드 8개 조회
    @Operation(summary = "실시간 인기 키워드 8개 불러오기 (수정중)")
    @GetMapping("/keywords")
    public List<PopularKeywordDto> getPopularKeywords() {
        return trendsService.getPopularKeywords();
    }

    // 관심 키워드 (로그인 전)
    @Operation(summary = "관심 키워드 - 로그인 전 (사회/정치/경제/연예)")
    @GetMapping("/interests/keywords")
    public List<KeywordDto> getDefaultInterestKeywords() {
        return trendsService.getPopularKeywordsByCategories(List.of("사회", "정치", "경제", "연예"));
    }

    // 관심 키워드 (로그인 후 - 맞춤형)
    @Operation(summary = "관심 키워드 - 로그인 후 (개인 맞춤 카테고리)")
    @GetMapping("/interests/{userId}/keywords")
    public List<KeywordDto> getUserInterestKeywords(@PathVariable Long userId) {
        List<String> userCategories = trendsService.getUserInterestCategories(userId);
        if (userCategories == null || userCategories.isEmpty()) {
            userCategories = List.of("사회", "정치", "경제", "연예");
        }
        return trendsService.getPopularKeywordsByCategories(userCategories);
    }
}


