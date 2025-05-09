package team.backend.curio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.users;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findAllByMembersContaining(users users);

}