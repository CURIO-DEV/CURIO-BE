package team.backend.curio.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
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

    @Column(name = "uadateAt", nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now(); //마지막 수정 시간
}
