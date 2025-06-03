package team.backend.curio.dto.NewsDTO;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;
import team.backend.curio.domain.News;

import java.time.LocalDateTime;

import static team.backend.curio.domain.QNews.news;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class NewsResponseDto {
    private Long articleId;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;  // 추가
    private LocalDateTime updatedAt;

    // 생성자
    public NewsResponseDto(News news) {
        this.articleId = news.getNewsId();
        this.title = news.getTitle();
        this.content = news.getContent();
        this.imageUrl = news.getImageUrl();  // 이미지 URL 추가
    }

    // 새로운 생성자 (title과 imageUrl을 받는 생성자 추가)
    public NewsResponseDto(String title, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
