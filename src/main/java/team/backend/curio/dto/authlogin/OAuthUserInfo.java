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

    private String oauthId;     // ← 공통 ID (카카오 user_id / 구글 sub)
    private int socialType;
}
