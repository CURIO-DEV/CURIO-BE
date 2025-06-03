package team.backend.curio.dto.NewsDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewsSummaryResponseDto {
    private Long articleId;
    private String title;
    private String summaryType;
    private String summary;

}
