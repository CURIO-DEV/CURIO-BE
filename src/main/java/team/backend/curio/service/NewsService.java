package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.dto.NewsDTO.InterestNewsResponseDto;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.dto.NewsDTO.CurioGoNewsResponseDto;
import team.backend.curio.dto.NewsDTO.NewsSummaryResponseDto;
import team.backend.curio.dto.NewsDTO.SearchNewsResponseDto;
import team.backend.curio.repository.NewsRepository;
import team.backend.curio.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final GptSummaryService gptSummaryService;

    @Autowired
    public NewsService(NewsRepository newsRepository, UserRepository userRepository, GptSummaryService gptSummaryService) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.gptSummaryService = gptSummaryService;
    }

    // 관심사에 맞는 뉴스 조회
    public List<InterestNewsResponseDto> getInterestNewsByUserId(Long userId) {
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<String> interests = List.of(
                user.getInterest1(),
                user.getInterest2(),
                user.getInterest3(),
                user.getInterest4()
        );

        List<InterestNewsResponseDto> responseList = new ArrayList<>();

        for (String interest : interests) {
            List<News> newsByInterest = newsRepository.findByCategory(interest);
            responseList.add(new InterestNewsResponseDto(interest, newsByInterest));
        }

        return responseList;
    }

    // 모든 뉴스 조회
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    // 관심사 이름으로 뉴스 조회
    public List<News> getNewsByInterest(Long userId, String interestName) {
        return newsRepository.findByCategory(interestName);
    }

    // 크롤링된 뉴스 여러 개 저장
    public void saveAllNews(List<News> newsList) {
        for (News news : newsList) {
            try {
                String content = news.getContent();
                if (content != null && !content.isBlank()) {
                    news.setSummaryShort(gptSummaryService.summarize(content, "short"));
                    news.setSummaryMedium(gptSummaryService.summarize(content, "medium"));
                    news.setSummaryLong(gptSummaryService.summarize(content, "long"));
                } else {
                    news.setSummaryShort("");
                    news.setSummaryMedium("");
                    news.setSummaryLong("");
                }
            } catch (Exception e) {
                System.err.println("Error summarizing news with title: " + news.getTitle());
                e.printStackTrace();
                // 필요 시 summary 필드를 빈 문자열 처리해서 저장할 수도 있음
                news.setSummaryShort("");
                news.setSummaryMedium("");
                news.setSummaryLong("");
            }
        }

        try {
            newsRepository.saveAll(newsList);
        } catch (Exception e) {
            System.err.println("Error saving news list to repository");
            e.printStackTrace();
            throw e;  // 다시 던져서 컨트롤러 쪽에서 처리하도록
        }
    }


    //특정 뉴스의 관련 뉴스 조회
    public List<News> getRelatedNews(Long articleId) {
        // 1. 기사 존재 여부 및 카테고리 조회
        String category = newsRepository.findCategoryById(articleId);
        if (category == null) {
            throw new IllegalArgumentException("해당 ID의 뉴스 기사가 존재하지 않거나 카테고리가 없습니다. articleId=" + articleId);
        }

        // 2. 관련 뉴스 조회 (News entity 반환)
        return newsRepository.findTop4RelatedNewsByCategory(category, articleId);
    }


    // 기사 헤드라인과 이미지 URL 조회
    public NewsResponseDto getArticleHeadline(Long articleId) {
        // 해당 기사 조회
        News news = newsRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        // 헤드라인과 이미지 URL, 생성일 수정일까지 반환
        return new NewsResponseDto(
                news.getTitle(),
                news.getImageUrl(),
                news.getCreatedAt(),
                news.getUpdatedAt()
        );
    }

    // 뉴스 요약 조회 기능 추가
    public NewsSummaryResponseDto getSummaryByType(Long articleId, String type) {
        News news = newsRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기사를 찾을 수 없습니다."));

        String summary;

        // 요청된 요약 타입에 따라 필드를 선택
        switch (type.toLowerCase()) {
            case "short":
                summary = news.getSummaryShort();
                break;
            case "medium":
                summary = news.getSummaryMedium();
                break;
            case "long":
                summary = news.getSummaryLong();
                break;
            default:
                throw new IllegalArgumentException("요약 타입은 short, medium, long 중 하나여야 합니다.");
        }

        // 요약이 null이거나 빈 문자열이면 예외 발생
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("요약 내용이 존재하지 않습니다.");
        }

        // 응답 DTO 생성하여 반환
        return new NewsSummaryResponseDto(news.getNewsId(), news.getTitle(), type, summary);
    }

    public Page<SearchNewsResponseDto> searchArticles(String query, Pageable pageable) {
        List<String> keywords = Arrays.stream(query.split("\\s+"))
                .filter(word -> !word.isBlank())
                .collect(Collectors.toList());

        return newsRepository.searchByKeywords(keywords, pageable);
    }

    public List<News> getNewsByInterestSortedByRecent(String category) {
        return newsRepository.findTop10ByCategoryOrderByCreatedAtDesc(category);
    }

    //[CurioGo] 사용자의 관심 카테고리를 기반으로 좋아요 수가 높은 뉴스 5개 조회
    public List<CurioGoNewsResponseDto> getCurioGoNewsByUserId(Long userId) {
        // 1. 유저 정보 조회
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. 관심사 리스트 생성
        List<String> interests = List.of(
                user.getInterest1(),
                user.getInterest2(),
                user.getInterest3(),
                user.getInterest4()
        );

        // 3. 관심사 리스트에 해당하는 뉴스 중 좋아요 순으로 상위 5개만 조회
        List<News> top5News = newsRepository.findTop5ByCategoryInOrderByLikeCountDesc(interests);

        // 4. DTO로 변환하여 반환
        return top5News.stream()
                .map(news -> new CurioGoNewsResponseDto(
                        news.getNewsId(),
                        news.getImageUrl(),
                        news.getSummaryMedium()
                ))
                .collect(Collectors.toList());
    }
}