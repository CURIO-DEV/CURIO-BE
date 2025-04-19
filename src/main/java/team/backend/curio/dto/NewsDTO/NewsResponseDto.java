package team.backend.curio.dto.NewsDTO;
import lombok.Getter;
import lombok.Setter;
import team.backend.curio.domain.News;

@Getter
@Setter
public class NewsResponseDto {
    private String title;
    private String content;
    private String imageUrl;

    // 생성자
    public NewsResponseDto(News news) {
        this.title = news.getTitle();
        this.content = news.getContent();
        this.imageUrl = news.getImageUrl();  // 이미지 URL 추가
    }

}
