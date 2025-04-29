package team.backend.curio.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Bookmark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    private String name;

    private String color;

    private String collaboratorEmail1;
    private String collaboratorEmail2;
    private String collaboratorEmail3;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private users users;  // 사용자와의 관계

    @ManyToMany
    @JoinTable(
            name = "bookmark_news",  // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "bookmark_id"),
            inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    private List<News> newsList = new ArrayList<>();


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateBookmark(String name, String color, String email1, String email2, String email3) {
        this.name = name;
        this.color = color;
        this.collaboratorEmail1 = email1;
        this.collaboratorEmail2 = email2;
        this.collaboratorEmail3 = email3;
    }

    public void addNews(News news) {
        this.newsList.add(news);
    }

    public void removeNews(News news) {
        this.newsList.remove(news);
    }

}



