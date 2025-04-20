package team.backend.curio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.domain.News;
import team.backend.curio.service.NewsService;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // 뉴스 저장 API
    @PostMapping("/crawler")
    public ResponseEntity<String> saveNews(@RequestBody List<News> newsList) {
        System.out.println("API 호출됨!");
        newsService.saveAllNews(newsList);
        return ResponseEntity.ok("크롤링 뉴스 저장 성공!");
    }

    @GetMapping("/list")
    public List<News> getNewsList() {
        return newsService.getAllNews();
    }

}
