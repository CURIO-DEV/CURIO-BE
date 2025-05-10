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
    private final GptSummaryService gptSummaryService;

    @Autowired
    public TrendsService(NewsRepository newsRepository,RestTemplate restTemplate,GptSummaryService gptSummaryService) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
        this.gptSummaryService = gptSummaryService;
    }

    public List<NewsWithCountsDto> getPopularArticles() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay(); // 오늘 자정 기준
        return newsRepository.findTop4ByCreatedAtAfterOrderByLikeCountDescCreatedAtDesc(todayStart)
                .stream()
                .map(NewsWithCountsDto::new)
                .collect(Collectors.toList());
    }

    // 인기 키워드 반환
    public List<PopularKeywordDto> getPopularKeywords() {
        // 1. 오늘 날짜 뉴스 조회
        LocalDate today = LocalDate.now();
        List<News> todayNews = newsRepository.findAllByCreatedAtBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        // 2. 뉴스 본문 이어붙이기
        StringBuilder contentBuilder = new StringBuilder();
        for (News news : todayNews) {
            if (news.getSummaryShort() != null) {
                contentBuilder.append(news.getSummaryShort()).append("\n\n");
            }
        }

        // 3. GPT에 키워드 추출 요청
        String prompt = "다음 뉴스 요약들을 참고해서, 지금 이슈가 되는 키워드 8개를 많이 나온거 같은 키워드 우선으로 순위 매겨서 반환해줘 " +
                "형식은 쉼표(,)로 구분해서 한 줄로 알려줘.\n\n" + contentBuilder;

        String gptResponse = gptSummaryService.callGptApi(prompt);

        // 4. 응답 파싱
        String[] keywords = gptResponse.split(",");
        return Arrays.stream(keywords)
                .limit(8)
                .map(String::trim)
                .map(PopularKeywordDto::new)
                .collect(Collectors.toList());
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

