package team.backend.curio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.curio.domain.UserAction;
import team.backend.curio.domain.UserActionId;

import java.util.Optional;

public interface UserActionRepository extends JpaRepository<UserAction, UserActionId> {
    //유저의 기사에 대한 행동(좋아요 추천) 저장/조회
    Optional<UserAction> findByUserIdAndNewsId(Long userId, Long newsId);

}