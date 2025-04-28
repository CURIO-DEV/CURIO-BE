package team.backend.curio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.curio.domain.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

}