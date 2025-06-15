package team.backend.curio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.backend.curio.client.GoogleOAuthClient;
import team.backend.curio.client.KakaoOAuthClient;
import team.backend.curio.domain.RefreshToken;
import team.backend.curio.domain.users;
import team.backend.curio.dto.UserDTO.TokenResponse;
import team.backend.curio.dto.authlogin.OAuthUserInfo;
import team.backend.curio.jwt.JwtUtil;
import team.backend.curio.repository.RefreshTokenRepository;
import team.backend.curio.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final GoogleOAuthClient googleOAuthClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final NicknameService nicknameService;


    // 카카오 로그인 후 토큰 발급
    public TokenResponse loginWithKakao(OAuthUserInfo kakaoUser) {
        users user = findOrCreateKakaoUser(kakaoUser);
        return issueTokens(user);
    }

    // 구글 로그인 후 토큰 발급
    public TokenResponse loginWithGoogle(OAuthUserInfo googleUser) {
        users user = findOrCreateGoogleUser(googleUser);
        return issueTokens(user);
    }

    // 기존 로직 유지
    public users findOrCreateKakaoUser(OAuthUserInfo kakaoUser) {
        return userRepository.findByEmail(kakaoUser.getEmail())
                .map(user -> {
                    // 🔥 기존 유저인데 oauthId가 없다면 저장 (예외 대응)
                    if (user.getOauthId() == null && kakaoUser.getOauthId() != null) {
                        user.setOauthId(kakaoUser.getOauthId());
                        userRepository.save(user);
                    }
                    return user;
                })
                .orElseGet(() -> {

                    users newUser = users.builder()
                            .email(kakaoUser.getEmail())
                            .nickname(nicknameService.generateNickname()) //자동 생성 닉네임
                            .profile_image_url(kakaoUser.getProfileImage())
                            .oauthId(kakaoUser.getOauthId())
                            .socialType(1)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    public users findOrCreateGoogleUser(OAuthUserInfo googleUser) {
        return userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {

                    users newUser = users.builder()
                            .email(googleUser.getEmail())
                            .nickname(nicknameService.generateNickname())
                            .profile_image_url(googleUser.getProfileImage())
                            .socialType(2)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    // JWT 발급 및 RefreshToken 저장 로직
    private TokenResponse issueTokens(users user) {
        String access = jwtUtil.createAccessToken(user);
        String refresh = jwtUtil.createRefreshToken(user);

        refreshTokenRepository.save(
                new RefreshToken(user.getEmail(), refresh)
        );

        return new TokenResponse(access, refresh);
    }
}
