package team.backend.curio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.users;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findAllByMembersContaining(users users);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM bookmark_news WHERE news_id = :newsId", nativeQuery = true)
    void deleteNewsRelations(Long newsId);
}