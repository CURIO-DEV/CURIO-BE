package team.backend.curio.dto.NewsDTO;

import lombok.*;
import team.backend.curio.domain.News;

import java.util.List;

@Getter
@Setter
public class InterestNewsResponseDto {
    private String interestName;
    private List<NewsResponseDto> newsList;

    public InterestNewsResponseDto(String interestName, List<News> newsEntities) {
        this.interestName = interestName;
        this.newsList = newsEntities.stream()
                .map(NewsResponseDto::new)
                .toList();
    }
}
