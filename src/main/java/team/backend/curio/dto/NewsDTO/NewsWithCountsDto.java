package team.backend.curio.dto.NewsDTO;

import lombok.Getter;
import team.backend.curio.domain.News;

@Getter
public class NewsWithCountsDto {
    private Long newsId;
    private String title;
    private String imageUrl;
    private int likeCount;
    private int saveCount;

    public NewsWithCountsDto(News news) {
        this.newsId=news.getNewsId();
        this.title = news.getTitle();
        this.imageUrl = news.getImageUrl();
        this.likeCount = news.getLikeCount();
        this.saveCount = news.getSaveCount(); // saveCount 필드 추가된 상태여야 함
    }
}
