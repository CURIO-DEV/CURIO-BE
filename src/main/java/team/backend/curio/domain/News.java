package team.backend.curio.domain;

<<<<<<< HEAD

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
=======
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
>>>>>>> news-api
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "News")
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

}