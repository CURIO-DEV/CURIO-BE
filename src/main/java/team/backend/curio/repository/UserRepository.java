package team.backend.curio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.curio.domain.users;
import java.util.List;


public interface UserRepository extends JpaRepository<users, Long> {
    // 추가적인 쿼리 메서드가 필요하면 여기에 작성

    // 뉴스레터 신청 상태가 1이고, 수신 이메일이 설정된 사용자 조회
    List<users> findByNewsletterStatusAndNewsletterEmailNotNull(int status);

}