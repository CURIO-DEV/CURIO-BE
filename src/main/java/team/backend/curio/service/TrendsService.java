package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.backend.curio.domain.News;
import team.backend.curio.dto.NewsDTO.NewsWithCountsDto;
import team.backend.curio.dto.PopularKeywordDto;
import team.backend.curio.repository.NewsRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrendsService {

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public TrendsService(NewsRepository newsRepository,RestTemplate restTemplate) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
    }

    public List<NewsWithCountsDto> getPopularArticles() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay(); // 오늘 자정 기준
        return newsRepository.findTop4ByCreatedAtAfterOrderByLikeCountDescCreatedAtDesc(todayStart)
                .stream()
                .map(NewsWithCountsDto::new)
                .collect(Collectors.toList());
    }

    // 새로운 메서드: 인기 키워드 반환
    public List<PopularKeywordDto> getPopularKeywords() {
        // GPT API가 들어오기 전에는 고정된 인기 키워드로 반환
        return Arrays.asList(
                new PopularKeywordDto("실트1"),
                new PopularKeywordDto("실트2"),
                new PopularKeywordDto("실트3"),
                new PopularKeywordDto("실트4"),
                new PopularKeywordDto("실트5"),
                new PopularKeywordDto("실트6"),
                new PopularKeywordDto("실트7"),
                new PopularKeywordDto("실트8")
        );
    }

    // 인기 게시글 4개 가져오기
    public List<News> getTrendingNews() {
        // 내부 API URL
        String url = "http://localhost:8080/curio/api/trends/popular-articles";  // 실제 API URL로 수정

        // 해당 URL로 API 요청하고 응답 받기
        News[] trendingNews = restTemplate.getForObject(url, News[].class);

        // null 체크 후 리스트 반환
        if (trendingNews != null) {
            return Arrays.asList(trendingNews);
        } else {
            return Arrays.asList();  // 빈 리스트 반환
        }
    }
}

