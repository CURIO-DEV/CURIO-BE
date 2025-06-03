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

    public SocialLoginResponseDto loginWithKakao(OAuthUserInfo kakaoUser) {

        // 1. DB에서 사용자 조회
        users user = userRepository.findByEmail(kakaoUser.getEmail())
                .orElseGet(() -> {
                    // 2. 신규 사용자 저장
                    users newUser = users.builder()
                            .email(kakaoUser.getEmail())
                            .nickname(kakaoUser.getNickname())
                            .profile_image_url(kakaoUser.getProfileImage())
                            .socialType(1) // (1 = 카카오) 유지
                            .build();
                    return userRepository.save(newUser);
                });

        return new SocialLoginResponseDto(user.getUserId(), user.getNickname(), user.getEmail());
    }

    public SocialLoginResponseDto loginWithGoogle(OAuthUserInfo googleUser) {
        // 1. DB에서 사용자 조회
        users user = userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    users newUser = users.builder()
                            .email(googleUser.getEmail())
                            .nickname(googleUser.getNickname())
                            .profile_image_url(googleUser.getProfileImage())
                            .socialType(2) // (2 = 구글)
                            .build();
                    return userRepository.save(newUser);
                });


        return new SocialLoginResponseDto(user.getUserId(), user.getNickname(), user.getEmail());
    }
}
