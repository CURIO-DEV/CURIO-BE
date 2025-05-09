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

    @ManyToMany
    @JoinTable(
            name = "bookmark_members",  // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "bookmark_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<users> members = new ArrayList<>();  // 유저 리스트

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


    public void updateBookmark(String name, String color/*, String email1, String email2, String email3*/) {
        this.name = name;
        this.color = color;
    }

    public void addNews(News news) {
        this.newsList.add(news);
    }

    public void removeNews(News news) {
        this.newsList.remove(news);
    }

    // 멤버 추가 메소드
    public void addMember(users users) {
        if (this.members.size() < 4) {
            this.members.add(users);
        }
    }

    // 멤버 삭제 메소드
    public void removeMember(users users) {
        this.members.remove(users);
    }

}



