package team.backend.curio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.backend.curio.client.GoogleOAuthClient;
import team.backend.curio.client.KakaoOAuthClient;
import team.backend.curio.domain.users;
import team.backend.curio.dto.authlogin.SocialLoginResponseDto;
import team.backend.curio.dto.authlogin.OAuthUserInfo;
import team.backend.curio.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final GoogleOAuthClient googleOAuthClient;
    private final UserRepository userRepository;

    // ✅ 변경 후: 사용자 엔티티 리턴
    public users findOrCreateKakaoUser(OAuthUserInfo kakaoUser) {
        return userRepository.findByEmail(kakaoUser.getEmail())
                .orElseGet(() -> {
                    users newUser = users.builder()
                            .email(kakaoUser.getEmail())
                            .nickname(kakaoUser.getNickname())
                            .profile_image_url(kakaoUser.getProfileImage())
                            .socialType(1) // 카카오
                            .build();
                    return userRepository.save(newUser);
                });
    }

    //public SocialLoginResponseDto loginWithGoogle(OAuthUserInfo googleUser) {
    public users findOrCreateGoogleUser(OAuthUserInfo googleUser) {
        return userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    users newUser = users.builder()
                            .email(googleUser.getEmail())
                            .nickname(googleUser.getNickname())
                            .profile_image_url(googleUser.getProfileImage())
                            .socialType(2) // 구글
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
