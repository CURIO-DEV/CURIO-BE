package team.backend.curio.dto.NewsDTO;

import lombok.Getter;
import team.backend.curio.domain.News;

@Getter
public class NewsWithCountsDto {
    private String title;
    private String content;
    private String imageUrl;
    private int likeCount;
    private int saveCount;

    public NewsWithCountsDto(News news) {
        this.title = news.getTitle();
        this.content = news.getContent();
        this.imageUrl = news.getImageUrl();
        this.likeCount = news.getLikeCount();
        this.saveCount = news.getSaveCount(); // saveCount 필드 추가된 상태여야 함
    }
}
