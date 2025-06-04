package team.backend.curio.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.*;

// 사용자가 뉴스 기사에 대해 남긴 행동 기록을 저장하는 엔티티
// 복합키로 user_id + news_id 조합을 사용함
@Entity
@IdClass(UserActionId.class) // 복합키 클래스 지정
@Table(name="user_action") // 테이블 이름이 "UserAction"으로 매핑되도록 지정
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 자동 생성
@Builder //빌더 패턴으로 객체 생성 가능

public class UserAction implements Serializable {

    // 사용자 ID (User 테이블의 PK와 연관)
    @Id
    @Column(name="user_id")
    private Long userId;

    // 뉴스 기사 ID (News 테이블의 PK와 연관)
    @Id
    @Column(name="news_id")
    private Long newsId;

    /* 좋아요 여부
     * true  → 사용자가 해당 뉴스에 '좋아요'를 누른 상태
     * false → 좋아요를 누르지 않았거나 취소한 상태
     */
    @Column(name="liked",nullable=false) //like는 SQL 예약어라 백틱으로 감싸줌
    private boolean like;

    /* 추천 상태 (vote 필드)
     *  1 → 추천
     * -1 → 비추천
     *  0 → 아무 것도 안 한 상태 (기본값)
     */
    @Column(nullable=false)
    private int vote=0;

    /* 사용자 행동이 처음 기록된 시점
     * 기본값은 현재 시간 (LocalDateTime.now())
     */
    @Column(name="createAt",nullable=false,updatable=false)
    private LocalDateTime createAt=LocalDateTime.now();

    /* 사용자 행동이 마지막으로 수정된 시점
     * 좋아요나 추천 상태가 바뀔 때마다 갱신됨
     */
    @Column(name="updateAt",nullable=false)
    private LocalDateTime updateAt=LocalDateTime.now();

    // 데이터베이스에 저장되기 전에 호출되는 메서드
    @PrePersist
    protected void onCreate() {
        // createAt이 null이면 현재시간으로 설정
        if(createAt==null){
            createAt=LocalDateTime.now();
        }
        // updateAt이 null이면 현재시간으로 설정
        if(updateAt==null){
            updateAt=LocalDateTime.now();
        }
    }
}
