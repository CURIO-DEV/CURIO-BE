package team.backend.curio.service;

import org.springframework.stereotype.Service;
import team.backend.curio.domain.News;
import team.backend.curio.dto.NewsDTO.NewsWithCountsDto;
import team.backend.curio.dto.PopularKeywordDto;
import team.backend.curio.repository.NewsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrendsService {

    private final NewsRepository newsRepository;

    public TrendsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<NewsWithCountsDto> getPopularArticles() {
        return newsRepository.findTop4ByOrderByLikeCountDescCreatedAtDesc()
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
}

