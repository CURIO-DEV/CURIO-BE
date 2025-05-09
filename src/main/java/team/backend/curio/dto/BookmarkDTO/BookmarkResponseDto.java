package team.backend.curio.dto.BookmarkDTO;

import team.backend.curio.domain.BookmarkColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

public class BookmarkResponseDto {
    private Long bookmarkId;
    private String name;
    private BookmarkColor color;
    private List<String> members; // 추가
}
