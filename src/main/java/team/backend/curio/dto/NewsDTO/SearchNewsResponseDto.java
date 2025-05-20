package team.backend.curio.dto.NewsDTO;

import lombok.Getter;

@Getter
public class SearchNewsResponseDto {
    private Long newsId;
    private String title;
    private String content;
    private String imageUrl;

    public SearchNewsResponseDto(Long newsId, String title, String content, String imageUrl) {
        this.newsId = newsId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
