package team.backend.curio.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.backend.curio.dto.NewsDTO.SearchNewsResponseDto;

import java.util.List;

public interface NewsSearchRepository {
    Page<SearchNewsResponseDto> searchByKeywords(List<String> keywords, Pageable pageable);
}
