package team.backend.curio.service;

import org.springframework.stereotype.Service;
import team.backend.curio.domain.News;
import team.backend.curio.dto.NewsDTO.NewsWithCountsDto;
import team.backend.curio.repository.NewsRepository;

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
}

