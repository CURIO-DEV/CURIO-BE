package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.curio.domain.News;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.repository.NewsRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsRepository NewsRepository;

    @Autowired
    public NewsService(NewsRepository newsRepository) {
        this.NewsRepository = newsRepository;
    }

    public List<News> getNewsByInterest(String interestName) {
        return NewsRepository.findTop10ByCategoryOrderByCreatedAtDesc(interestName);
    }
}
