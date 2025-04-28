package team.backend.curio.dto.BookmarkDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateBookmarkDto {
    private String name;               // 폴더 이름
    private String color;              // 폴더 색상
    private String collaboratorEmail1; // 공동 작업자 1
    private String collaboratorEmail2; // 공동 작업자 2
    private String collaboratorEmail3; // 공동 작업자 3
}
