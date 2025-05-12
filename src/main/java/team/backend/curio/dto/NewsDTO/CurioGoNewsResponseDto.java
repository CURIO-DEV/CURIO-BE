package team.backend.curio.dto.NewsDTO;

import lombok.*;

@Getter
@AllArgsConstructor
public class CurioGoNewsResponseDto {
    private Long newsId;
    private String imageUrl;
    private String summaryMedium;
}
