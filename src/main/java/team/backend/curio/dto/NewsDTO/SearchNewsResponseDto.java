package team.backend.curio.dto.NewsDTO;

import lombok.Getter;
import team.backend.curio.domain.News;

@Getter
public class SearchNewsResponseDto {
    private Long articleId;
    private String title;
    private String content;
    private String imageUrl;

    public SearchNewsResponseDto(Long articleId, String title, String content, String imageUrl) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
