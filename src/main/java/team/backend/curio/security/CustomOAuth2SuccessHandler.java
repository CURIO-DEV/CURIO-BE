package team.backend.curio.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team.backend.curio.domain.users;
import team.backend.curio.repository.UserRepository;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final long expiration = 1000 * 60 * 60; // 1시간

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // OAuth2 로그인 타입을 구분: registrationId = "kakao" 또는 "google"
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        String email;
        String nickname;
        String profileImageUrl;

        // 카카오 로그인일 경우
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");
            profileImageUrl = (String) profile.get("profile_image_url");
        } else if ("google".equals(registrationId)) {
            email = oAuth2User.getAttribute("email");
            nickname = oAuth2User.getAttribute("name");
            profileImageUrl = oAuth2User.getAttribute("picture");
        } else {
            // 예외처리: 알 수 없는 소셜 로그인
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원하지 않는 소셜 로그인입니다.");
            return;
        }

        // 사용자 등록 or 조회
        users user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new users(email, nickname)));

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user);

        // 프론트로 리디렉션 (토큰 포함)
        response.sendRedirect("http://www.curi-o.site/login/success?token=" + token);

    }
}
