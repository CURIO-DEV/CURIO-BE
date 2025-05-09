package team.backend.curio.dto.BookmarkDTO;

import team.backend.curio.domain.BookmarkColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class BookmarkResponseDto {
    private Long bookmarkId;
    private String name;
    private BookmarkColor color;
}
