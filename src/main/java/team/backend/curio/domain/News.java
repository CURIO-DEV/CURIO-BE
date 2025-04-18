package team.backend.curio.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "News")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    private String title;

    private String content;

    @Column(name = "summary_short")
    private String summaryShort;

    @Column(name = "summary_medium")
    private String summaryMedium;

    @Column(name = "summary_long")
    private String summaryLong;

    private String category;

    @Column(name = "like_count")
    private int likeCount;

    //private String newsKeyword;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "source_url") //원본 기사링크
    private String sourceUrl;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getNewsId() {
        return newsId;
    }
}
