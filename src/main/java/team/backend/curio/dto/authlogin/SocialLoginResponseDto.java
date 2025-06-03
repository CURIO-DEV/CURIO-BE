package team.backend.curio.dto.authlogin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginResponseDto {
    private Long userId;
    private String nickname;
    private String email;
}
