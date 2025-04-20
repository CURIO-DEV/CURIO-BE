package team.backend.curio.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import team.backend.curio.dto.NewsDTO.NewsWithCountsDto;
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
    @GetMapping("/popular-articles")
    public List<NewsWithCountsDto> getPopularArticles() {
        return trendsService.getPopularArticles();
    }
}


