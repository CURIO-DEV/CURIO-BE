package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.backend.curio.domain.News;
import team.backend.curio.dto.KeywordDto;
import team.backend.curio.dto.NewsDTO.NewsWithCountsDto;
import team.backend.curio.dto.PopularKeywordDto;
import team.backend.curio.repository.NewsRepository;
import team.backend.curio.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrendsService {

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;
    private final GptSummaryService gptSummaryService;
    private final UserRepository userRepository;
    private String cachedKeywords = null;
    private LocalDate lastCacheDate = null;

    @Autowired
    public TrendsService(NewsRepository newsRepository,RestTemplate restTemplate,GptSummaryService gptSummaryService, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
        this.gptSummaryService = gptSummaryService;
        this.userRepository = userRepository;
    }

    public List<NewsWithCountsDto> getPopularArticles() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay(); // 오늘 자정 기준
        return newsRepository.findTop4ByCreatedAtAfterOrderByLikeCountDescCreatedAtDesc(todayStart)
                .stream()
                .map(NewsWithCountsDto::new)
                .collect(Collectors.toList());
    }

    public List<PopularKeywordDto> getPopularKeywords() {
        LocalDate today = LocalDate.now();

        // 오늘 요청이 이미 처리된 경우, 캐시된 결과 사용
        if (lastCacheDate != null && lastCacheDate.equals(today) && cachedKeywords != null) {
            return parseKeywordResponse(cachedKeywords);
        }

        // 1. 오늘 날짜 뉴스 조회
        List<News> todayNews = newsRepository.findAllByCreatedAtBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        // 2. 뉴스 요약 이어붙이기
        StringBuilder contentBuilder = new StringBuilder();
        for (News news : todayNews) {
            if (news.getSummaryShort() != null) {
                contentBuilder.append(news.getSummaryShort()).append("\n\n");
            }
        }

        // 3. GPT 프롬프트 구성
        String prompt = "다음 뉴스 요약들을 참고해서, 지금 이슈가 되는 키워드 8개를 많이 나온 거 같은 키워드 우선으로 순위 매겨서 반환해줘. " +
                "형식은 쉼표(,)로 구분해서 한 줄로 알려줘.\n\n" + contentBuilder;

        // 4. GPT 호출
        String gptResponse = gptSummaryService.callGptApi(prompt);

        // 5. 캐시 저장
        cachedKeywords = gptResponse;
        lastCacheDate = today;

        // 6. 결과 파싱 및 반환
        return parseKeywordResponse(gptResponse);
    }

    private List<PopularKeywordDto> parseKeywordResponse(String response) {
        String[] keywords = response.split(",");
        List<String> keywordList = Arrays.stream(keywords)
                .limit(8)
                .map(String::trim)
                .collect(Collectors.toList());

        return Collections.singletonList(new PopularKeywordDto(keywordList));
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
            return Arrays.asList();
        }
    }

    public List<KeywordDto> getPopularKeywordsByCategories(List<String> categories) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<News> newsList = newsRepository.findByCategoryInAndCreatedAtAfter(categories, todayStart);

        StringBuilder combinedSummaries = new StringBuilder();
        for (News news : newsList) {
            combinedSummaries.append(news.getSummaryShort()).append("\n");
        }

        String prompt = "다음 뉴스 요약들을 기반으로, **구체적이고 의미 있는 사건/인물/조직/정책 등**을 대표하는 중요한 키워드만 최대 20개 추출해줘. \n" +
                "'한국', '경제', '내년'처럼 너무 일반적이거나 추상적인 단어는 제외해줘. \n" +
                "각 키워드에 대해 중요도(1~100)를 부여하고, **아래 JSON 형식**으로 정확히 반환해줘:\n" +
                "[{\"keyword\": \"대통령\", \"weight\": 92}, {\"keyword\": \"물가\", \"weight\": 84}, ...]\n\n" +
                combinedSummaries;

        return gptSummaryService.callGptForKeywordExtraction(prompt);
    }

    public List<String> getUserInterestCategories(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    List<String> interests = new ArrayList<>();
                    if (user.getInterest1() != null) interests.add(user.getInterest1());
                    if (user.getInterest2() != null) interests.add(user.getInterest2());
                    if (user.getInterest3() != null) interests.add(user.getInterest3());
                    if (user.getInterest4() != null) interests.add(user.getInterest4());
                    return interests;
                })
                .orElse(Collections.emptyList());
    }

}

