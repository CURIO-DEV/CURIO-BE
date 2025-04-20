package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
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
}


