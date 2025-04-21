package team.backend.curio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.curio.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // 추가적인 쿼리 메서드가 필요하면 여기에 작성
}

