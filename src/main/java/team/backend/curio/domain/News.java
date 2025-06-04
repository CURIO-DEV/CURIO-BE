package team.backend.curio.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import team.backend.curio.dto.NewsDTO.NewsWithCountsDto;
import jakarta.persistence.*;
import lombok.*;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@QueryEntity
@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    private String title;

    @Lob
    private String content;

    @Column(name = "summary_short")
    @JsonProperty("summary_short")
    private String summaryShort;

    @Column(name = "summary_medium")
    @JsonProperty("summary_medium")
    private String summaryMedium;

    @Lob
    @Column(name = "summary_long")
    @JsonProperty("summary_long")
    private String summaryLong;

    private String category;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "save_count", nullable = false)
    private int saveCount;
    //private String newsKeyword;

    @Column(name = "image_url")
    @JsonProperty("imageUrl")
    private String imageUrl;

    @Column(name = "source_url")
    @JsonProperty("sourceUrl")
    private String sourceUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    // NewsWithCountsDto를 받는 생성자 추가
    public News(NewsWithCountsDto dto) {
        this.newsId = dto.getArticleId();
        this.title = dto.getTitle();
    }
}