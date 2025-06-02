package team.backend.curio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.backend.curio.client.KakaoOAuthClient;
import team.backend.curio.domain.users;
import team.backend.curio.dto.authlogin.KakaoLoginResponseDto;
import team.backend.curio.dto.authlogin.OAuthUserInfo;
import team.backend.curio.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    //private final GoogleOAuthClient googleOAuthClient;
    private final UserRepository userRepository;

    public KakaoLoginResponseDto loginWithKakao(OAuthUserInfo kakaoUser) {

        // 1. DB에서 사용자 조회
        users user = userRepository.findByEmail(kakaoUser.getEmail())
                .orElseGet(() -> {
                    // 2. 신규 사용자 저장
                    users newUser = users.builder()
                            .email(kakaoUser.getEmail())
                            .nickname(kakaoUser.getNickname())
                            .profile_image_url(kakaoUser.getProfileImage())
                            .socialType(1) // ⭐️ (1 = 카카오) 유지
                            .build();
                    return userRepository.save(newUser);
                });

        return new KakaoLoginResponseDto(user.getUserId(), user.getNickname(), user.getEmail());
    }
}
