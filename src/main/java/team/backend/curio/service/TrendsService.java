package team.backend.curio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private int lastNewsCount = -1;

    private LocalDate lastCategoryCacheDate;
    private List<String> lastCategoryCache;
    private int lastCategoryNewsCount;
    private String cachedCategoryKeywordJson;

    @Autowired
    public TrendsService(NewsRepository newsRepository,RestTemplate restTemplate,GptSummaryService gptSummaryService, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
        this.gptSummaryService = gptSummaryService;
        this.userRepository = userRepository;
    }

    public List<NewsWithCountsDto> getPopularArticles() {
        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        return newsRepository.findTop4ByCreatedAtBetweenOrderByLikeCountDescCreatedAtDesc(yesterdayStart,todayStart)
                .stream()
                .map(NewsWithCountsDto::new)
                .collect(Collectors.toList());
    }

    public List<PopularKeywordDto> getPopularKeywords() {
        LocalDate today = LocalDate.now();

        List<News> todayNews = newsRepository.findAllByCreatedAtBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        // 뉴스 개수가 이전과 같으면 캐시 사용
        if (lastCacheDate != null && lastCacheDate.equals(today) &&
                cachedKeywords != null && lastNewsCount == todayNews.size()) {
            return parseKeywordResponse(cachedKeywords);
        }

        // 요약 이어붙이기
        StringBuilder contentBuilder = new StringBuilder();
        for (News news : todayNews) {
            if (news.getSummaryShort() != null) {
                contentBuilder.append(news.getSummaryShort()).append("\n\n");
            }
        }

        String prompt = "다음 뉴스 요약들을 참고해서, 지금 이슈가 되는 키워드 8개를 많이 나온 거 같은 키워드 우선으로 순위 매겨서 반환해줘. " +
                "형식은 번호를 제외하고 단어를 쉼표(,)로 구분해서 한 줄로 알려줘.\n\n" +contentBuilder;

        String gptResponse = gptSummaryService.callGptApi(prompt);

        // 캐시 갱신
        cachedKeywords = gptResponse;
        lastCacheDate = today;
        lastNewsCount = todayNews.size();

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

        if (lastCategoryCacheDate != null &&
                lastCategoryCacheDate.equals(LocalDate.now()) &&
                lastCategoryCache != null &&
                lastCategoryCache.containsAll(categories) &&
                categories.containsAll(lastCategoryCache) &&
                newsList.size() == lastCategoryNewsCount &&
                cachedCategoryKeywordJson != null) {

            return parseKeywordJson(cachedCategoryKeywordJson);  // 기존 캐시 반환
        }

        StringBuilder combinedSummaries = new StringBuilder();
        for (News news : newsList) {
            if (news.getSummaryShort() != null) {
                combinedSummaries.append(news.getSummaryShort()).append("\n");
            }
        }

        String prompt = "다음 뉴스 요약들을 기반으로, 구체적이고 의미 있는 사건/인물/조직/정책 등의 중요한 키워드만 최대 20개 추출해줘. " +
                "‘한국’, ‘경제’, ‘내년’처럼 일반적이거나 추상적인 단어는 제외해줘. " +
                "각 키워드에 대해 중요도(1~100)를 부여해. " +
                "반환 형식은 순수한 JSON 배열이어야 하며, 마크다운 코드 블록(예: ```json)은 절대 포함하지 마:\n" +
                "[{\"keyword\": \"대통령\", \"weight\": 92}, {\"keyword\": \"물가\", \"weight\": 84}, ...]\n\n" +
                combinedSummaries;

        String gptResponse = gptSummaryService.callGptApi(prompt); // raw JSON string

        lastCategoryCacheDate = LocalDate.now();
        lastCategoryCache = new ArrayList<>(categories); // 리스트 복사
        lastCategoryNewsCount = newsList.size();
        cachedCategoryKeywordJson = gptResponse;

        return parseKeywordJson(gptResponse);
    }

    private List<KeywordDto> parseKeywordJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<KeywordDto>>() {});
        } catch (Exception e) {
            throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage(), e);
        }
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

