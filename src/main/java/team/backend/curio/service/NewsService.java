package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.dto.NewsDTO.InterestNewsResponseDto;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.dto.NewsDTO.CurioGoNewsResponseDto;
import team.backend.curio.dto.NewsDTO.NewsSummaryResponseDto;
import team.backend.curio.dto.NewsDTO.SearchNewsResponseDto;
import team.backend.curio.repository.BookmarkRepository;
import team.backend.curio.repository.NewsRepository;
import team.backend.curio.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final GptSummaryService gptSummaryService;
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public NewsService(NewsRepository newsRepository, UserRepository userRepository, GptSummaryService gptSummaryService, BookmarkRepository bookmarkRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.gptSummaryService = gptSummaryService;
        this.bookmarkRepository = bookmarkRepository;
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

        List<News> toSave = new ArrayList<>();

        for (News news : newsList) {

            // -------------------------------
            // 1) URL 또는 제목으로 기존 기사 검색
            // -------------------------------
            News existing = newsRepository.findBySourceUrl(news.getSourceUrl())
                    .orElseGet(() -> newsRepository.findByTitle(news.getTitle()).orElse(null));

            // ===============================
            // 2) 이미 존재하던 기사라면? → 업데이트 처리
            // ===============================
            if (existing != null) {

                // 내용이 달라진 경우에만 업데이트
                if (!Objects.equals(existing.getContent(), news.getContent())) {
                    existing.setContent(news.getContent());
                    existing.setCategory(news.getCategory());
                    existing.setImageUrl(news.getImageUrl());
                    existing.setUpdatedAt(news.getUpdatedAt());

                    // 요약 다시 생성
                    try {
                        existing.setSummaryShort(gptSummaryService.summarize(news.getContent(), "short"));
                        existing.setSummaryMedium(gptSummaryService.summarize(news.getContent(), "medium"));
                        existing.setSummaryLong(gptSummaryService.summarize(news.getContent(), "long"));
                    } catch (Exception e) {
                        existing.setSummaryShort("");
                        existing.setSummaryMedium("");
                        existing.setSummaryLong("");
                    }

                    toSave.add(existing);   // 업데이트된 기사 저장 대상에 추가
                }

                continue;  // 신규 저장 로직은 패스
            }

            // ===============================
            // 3) 새로운 기사라면? → 요약 생성 후 저장 리스트에 추가
            // ===============================
            try {
                news.setSummaryShort(gptSummaryService.summarize(news.getContent(), "short"));
                news.setSummaryMedium(gptSummaryService.summarize(news.getContent(), "medium"));
                news.setSummaryLong(gptSummaryService.summarize(news.getContent(), "long"));
            } catch (Exception e) {
                news.setSummaryShort("");
                news.setSummaryMedium("");
                news.setSummaryLong("");
            }

            toSave.add(news);
        }

        // -------------------------------
        // 4) 최종 저장 처리
        // -------------------------------
        if (!toSave.isEmpty()) {
            newsRepository.saveAll(toSave);
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
        List<String> interests = Stream.of(
                user.getInterest1(),
                user.getInterest2(),
                user.getInterest3(),
                user.getInterest4()
                ).filter(Objects::nonNull)  //null 제거
                .collect(Collectors.toList());

        if (interests.isEmpty()) {
            // 기본 관심사로 대체
            interests = List.of("사회", "정치", "경제", "연예");
        }

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

    @Transactional
    public int cleanupDuplicateNews() {
        List<Object[]> duplicates = newsRepository.findDuplicateUrls();
        int deleteCount = 0;

        for (Object[] row : duplicates) {
            String url = (String) row[0];

            List<News> sameNews = newsRepository.findBySourceUrlOrderByCreatedAtAsc(url);

            // 첫 번째 뉴스는 keep
            for (int i = 1; i < sameNews.size(); i++) {
                News duplicate = sameNews.get(i);

                // 1) 북마크 연결 먼저 제거
                bookmarkRepository.deleteNewsRelations(duplicate.getNewsId());

                // 2) 뉴스 삭제
                newsRepository.delete(duplicate);
                deleteCount++;
            }
        }

        return deleteCount;
    }

}