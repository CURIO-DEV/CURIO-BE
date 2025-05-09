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

    @Enumerated(EnumType.STRING) // enum
    @Column(nullable=false)
    private BookmarkColor color;

    @Column(name = "collaborator_email_1")
    private String collaboratorEmail1;

    @Column(name = "collaborator_email_2")
    private String collaboratorEmail2;

    @Column(name = "collaborator_email_3")
    private String collaboratorEmail3;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private users users;  // 사용자와의 관계

    @ManyToMany
    @JoinTable(
            name = "bookmark_news",  // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "bookmark_id"),
            inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    private List<News> newsList = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateBookmark(String name, BookmarkColor color, String email1, String email2, String email3) {
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



