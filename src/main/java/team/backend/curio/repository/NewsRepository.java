package team.backend.curio.repository;

import team.backend.curio.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    // 관심사(category)별로 createdAt 내림차순으로 상위 10개의 뉴스 조회
    List<News> findTop10ByCategoryOrderByCreatedAtDesc(String category);
}
