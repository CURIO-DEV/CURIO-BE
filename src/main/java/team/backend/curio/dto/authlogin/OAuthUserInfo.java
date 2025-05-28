package team.backend.curio.dto.authlogin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OAuthUserInfo {
    private String email;
    private String nickname;
    private String profileImage;
}
