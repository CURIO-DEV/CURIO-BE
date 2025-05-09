package team.backend.curio.dto.BookmarkDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateBookmarkDto {
    private String name;               // 폴더 이름
    private String color;              // 폴더 색상
    private List<String> members;  // members 필드를
}
