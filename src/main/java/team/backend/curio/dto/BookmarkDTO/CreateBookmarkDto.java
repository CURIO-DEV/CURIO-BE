package team.backend.curio.dto.BookmarkDTO;

import team.backend.curio.domain.BookmarkColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateBookmarkDto {
    private String name;               // 폴더 이름
    private BookmarkColor color;              // 폴더 색상
    private List<String> members;  // members 필드를
}
