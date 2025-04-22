package team.backend.curio.domain;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
=======
import jakarta.persistence.*;
import lombok.*;
>>>>>>> userAciton-api

import java.time.LocalDateTime;

@Entity
<<<<<<< HEAD
@Table(name = "News")
@Getter
@Setter
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    private String title;

    private String content;

    @Column(name = "summary_short")
    @JsonProperty("summary_short")
    private String summaryShort;

    @Column(name = "summary_medium")
    @JsonProperty("summary_medium")
    private String summaryMedium;

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
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

=======
@Table(name="News")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class News {
    @Id //기본키 PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto_increment와 매핑
    private Long newsId; //뉴스 고유 id

    @Column(nullable = false)
    private String title = "뉴스 제목"; //뉴스 제목

    @Column(nullable = false)
    private String content = "뉴스 내용"; //뉴스 본문

    private String summaryShort; //3줄
    private String summaryMedium; //5줄
    private String summaryLong; //7줄

    private String cartegory; //뉴스 카테고리

    @Column(nullable = false)
    private int likeCount = 0; //좋아요 수

    private String newsKeyword;

    @Column(name = "createAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); //생성 시간

    @Column(name = "updateAt", nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now(); //마지막 수정 시간
>>>>>>> userAciton-api
}
