package team.backend.curio.dto.NewsDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchNewsResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
}
