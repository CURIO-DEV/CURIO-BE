package team.backend.curio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long userId;  // 유저 ID
    private String nickname;  // 닉네임
    private String email;  // 이메일
}
