package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.dto.NewsDTO.InterestNewsResponseDto;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.dto.NewsDTO.RelatedNewsResponse;
import team.backend.curio.repository.NewsRepository;
import team.backend.curio.repository.UserRepository;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;


    @Autowired
    public NewsService(NewsRepository newsRepository, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
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
    public List<News> getNewsByInterest(String interestName) {
        return newsRepository.findByCategory(interestName);
    }

    // 크롤링된 뉴스 여러 개 저장
    public void saveAllNews(List<News> newsList) {
        newsRepository.saveAll(newsList);  // 여러 개의 뉴스 저장
    }

    //특정 뉴스의 관련 뉴스 조회
    public List<RelatedNewsResponse> getRelatedNews(Long articleId){
        // 먼저 해당 기사 id로 카테고리조회
        String category=newsRepository.findCategoryById(articleId);

        // 해당 카테고리로 관련 기사들을 조회
        List<News> relatedNews = newsRepository.findByCategory(category);

        // 관련 뉴스 데이터를 DTO로 변환하여 반환
        return relatedNews.stream()
                .map(news -> new RelatedNewsResponse(
                        news.getTitle(),
                        news.getImageUrl(),
                        news.getLikeCount(),
                        news.getSaveCount()
                ))
                .collect(Collectors.toList()); // Stream을 List로 변환
    }

    // 기사 헤드라인과 이미지 URL 조회
    public NewsResponseDto getArticleHeadline(Long articleId) {
        // 해당 기사 조회
        News news = newsRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        // 헤드라인과 이미지 URL 반환
        return new NewsResponseDto(news.getTitle(), news.getImageUrl());
    }
}

