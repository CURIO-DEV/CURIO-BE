package team.backend.curio.dto.BookmarkDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.backend.curio.domain.News;


import static team.backend.curio.domain.QNews.news;

@Getter
@AllArgsConstructor
public class BookmarkNewsDto {
    private Long articleId;
    private String title;
    private String content;
    private String imageUrl;

    public BookmarkNewsDto(News news){
        this.articleId = news.getNewsId();
        this.title = news.getTitle();
        this.content = news.getContent();
        this.imageUrl = news.getImageUrl();
    }

}