package team.backend.curio.repository;

import team.backend.curio.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long>, NewsSearchRepository {

    // 관심사(category)별로 createdAt 내림차순으로 상위 10개의 뉴스 조회
    List<News> findTop10ByCategoryOrderByCreatedAtDesc(String category);

    @Query("SELECT n FROM News n WHERE n.createdAt >= :start AND n.createdAt < :end")
    List<News> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<News> findByCategory(String category);

    // 좋아요 수로 내림차순 정렬, 같으면 최신순으로 정렬
    // List<News> findTop4ByOrderByLikeCountDescCreatedAtDesc();

    // 오늘 날짜 기준 + 좋아요 수로 내림차순 정렬, 같으면 최신순으로 정렬
    List<News> findTop4ByCreatedAtAfterOrderByLikeCountDescCreatedAtDesc(LocalDateTime todayStart);

    // 카테고리가 포함되어 있고, 날짜가 일치하는 뉴스 기사들 조회
    List<News> findByCategoryInAndCreatedAtAfter(List<String> categories, LocalDateTime dateTime);

    // 특정 뉴스의 카테고리 조회
    @Query("SELECT n.category FROM News n WHERE n.newsId = :articleId")
    String findCategoryById(Long articleId);

    // 해당 카테고리로 관련 뉴스 조회
    @Query(value="SELECT * FROM News WHERE category = :category AND news_id <> :excludeId ORDER BY like_count DESC LIMIT 4", nativeQuery=true)
    List<News> findTop4RelatedNewsByCategory(@Param("category") String category, @Param("excludeId") Long excludeId);

    // 관심 카테고리 안에서 좋아요 많은 뉴스 상위 5개
    List<News> findTop5ByCategoryInOrderByLikeCountDesc(List<String> categories);
}
