package team.backend.curio.dto.BookmarkDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class BookmarkResponseDto {
    private Long bookmarkId;
    private String name;
    private String color;
}
