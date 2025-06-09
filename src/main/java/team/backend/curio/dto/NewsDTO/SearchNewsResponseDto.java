package team.backend.curio.dto.NewsDTO;

import lombok.Getter;
import team.backend.curio.domain.News;

@Getter
public class SearchNewsResponseDto {
    private Long newsId;
    private String title;
    private String content;
    private String imageUrl;

    public SearchNewsResponseDto(News news) {
        this.newsId = news.getNewsId();
        this.title = news.getTitle();
        this.content = news.getContent();
        this.imageUrl = news.getImageUrl();
    }
}
