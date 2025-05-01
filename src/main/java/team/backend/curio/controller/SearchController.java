package team.backend.curio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.dto.NewsDTO.SearchNewsResponseDto;
import team.backend.curio.service.NewsService;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor

public class SearchController {
    private final NewsService newsService;

    @GetMapping("/search")
    public Page<SearchNewsResponseDto> search(
            @RequestParam String query,
            @RequestParam String type,
            Pageable pageable
    ) {
        if (type.equalsIgnoreCase("news")) {
            List<String> keywords = Arrays.stream(query.split("\\s+"))
                    .filter(word -> !word.isBlank())
                    .collect(Collectors.toList());
            return newsService.searchArticles(query, pageable);
        }

        throw new IllegalArgumentException("지원하지 않는 검색 타입입니다: " + type);
    }
}