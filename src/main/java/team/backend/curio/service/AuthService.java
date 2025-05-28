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

    public KakaoLoginResponseDto loginWithKakao(String code) {
        // 1. 토큰 요청
        String accessToken = kakaoOAuthClient.getAccessToken(code);

        // 2. 사용자 정보 요청
        OAuthUserInfo kakaoUser = kakaoOAuthClient.getUserInfo(accessToken);

        // 3. DB 저장 or 조회
        users user = userRepository.findByEmail(kakaoUser.getEmail())
                .orElseGet(() -> {
                    users newUser = users.builder()
                            .email(kakaoUser.getEmail())
                            .nickname(kakaoUser.getNickname())
                            .profile_image_url(kakaoUser.getProfileImage())
                            .socialType(1)
                            .build();
                    return userRepository.save(newUser);
                });

        return new KakaoLoginResponseDto(user.getUserId(), user.getNickname(), user.getEmail());
    }
}
