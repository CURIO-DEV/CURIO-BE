package team.backend.curio.dto.NewsDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsRequestDto {
    private String title;
    private String content;
    private String summaryShort;
    private String summaryMedium;
    private String summaryLong;
    private String category;
    private String imageUrl;
    private String sourceUrl;
}
