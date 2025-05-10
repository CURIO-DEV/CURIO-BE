package team.backend.curio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.backend.curio.domain.News;
import team.backend.curio.repository.NewsRepository;

@Service
@RequiredArgsConstructor
public class NewsSummaryService {
    private final NewsRepository newsRepository;
    private final GptSummaryService gptSummaryService;

    @Transactional
    public void summarizeAndSaveNews(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("뉴스 없음"));

        String content = news.getContent();

        String shortSummary = gptSummaryService.summarize(content, "short");
        String mediumSummary = gptSummaryService.summarize(content, "medium");
        String longSummary = gptSummaryService.summarize(content, "long");

        news.setSummaryShort(shortSummary);
        news.setSummaryMedium(mediumSummary);
        news.setSummaryLong(longSummary);
        newsRepository.save(news);
    }

}
