package team.backend.curio.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`users`") // 테이블 이름이 "User"로 매핑되도록 지정
@Getter
@Setter
@NoArgsConstructor      // 기본 생성자
@AllArgsConstructor     // 모든 필드를 인자로 받는 생성자
@Builder                // 빌더 패턴 사용 가능하게
public class users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "user_id")
    private Long userId;

    private String nickname; // 랜덤 닉네임

    private String email; //소셜로그인에 사용된 이메일

    @Column(name = "social_type")
    private Integer socialType; // 카카오, 구글

    @Column(name = "newsletter_email")
    private String newsletterEmail; //뉴스레터 받을 이메일

    @Column(columnDefinition = "int default 2")
    private int summaryPreference = 2; // 요약 길이 선호도 (1=짧음, 2=보통, 3=김)

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 관심사 최대 4개
    private String interest1;
    private String interest2;
    private String interest3;
    private String interest4;

    private String fontSize;
  
    private String profile_image_url;  // 프로필 사진 URL 필드 추가

    @Column(name = "newsletter_status", nullable = false, columnDefinition = "int default 0") // 0이면 구독 안함, 1이면 구독함
    private int newsletterStatus; // 뉴스레터 구독 상태 (0: 구독 안함, 1: 구독함)

    // 사용자와 북마크 관계 설정
    @ManyToMany(mappedBy = "members") // "members"로 수정
    private List<Bookmark> bookmarks = new ArrayList<>();

    public users(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;

        //기본정보 설정
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.socialType = 1;
        this.newsletterEmail = email;
        this.summaryPreference = 2;
        this.newsletterStatus = 0;

        //관심사 null
        this.interest1 = null;
        this.interest2 = null;
        this.interest3 = null;
        this.interest4 = null;

        //폰트,프로필 null
        this.fontSize = null;
        this.profile_image_url = null;

        //북마크 리스트 초기화
        this.bookmarks = new ArrayList<>();
    }

}
