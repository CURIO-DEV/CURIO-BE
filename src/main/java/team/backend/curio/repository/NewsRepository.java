package team.backend.curio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.curio.domain.News;

public interface NewsRepository extends JpaRepository<News, Long>{
}
