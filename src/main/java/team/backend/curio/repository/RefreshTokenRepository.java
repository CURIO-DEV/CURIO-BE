package team.backend.curio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.curio.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
}
