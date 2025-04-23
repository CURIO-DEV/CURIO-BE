package team.backend.curio.dto.NewsDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // 생성자를 자동으로 만들어주는 Lombok 어노테이션

public class RelatedNewsResponse {

    private String title;          // 기사 제목 (헤드라인)
    private String imageUrl;       // 기사 이미지 URL
    private int likeCount;         // 좋아요 수
    private int saveCount;         // 스크랩 수

}
