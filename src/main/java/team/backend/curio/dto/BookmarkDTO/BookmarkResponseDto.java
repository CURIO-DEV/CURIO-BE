package team.backend.curio.dto.BookmarkDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

public class BookmarkResponseDto {
    private Long bookmarkId;
    private String name;
    private String color;
    private List<String> members; // 추가

}
