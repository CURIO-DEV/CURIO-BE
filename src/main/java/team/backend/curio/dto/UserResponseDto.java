package team.backend.curio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long userId;  // 유저 ID
    private String nickname;  // 닉네임
    private String email;  // 이메일
    private String profile_image_url;  // 프로필 사진 URL 추가

    // 기존에 정의된 기본 생성자가 필요한 경우
    public UserResponseDto()
    {
        // 기본 생성자
    }

    // 기본 생성자
    public UserResponseDto(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profile_image_url = profileImageUrl;
    }
}
