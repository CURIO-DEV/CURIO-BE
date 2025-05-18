package team.backend.curio.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team.backend.curio.domain.users;
import team.backend.curio.repository.UserRepository;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final long expiration = 1000 * 60 * 60; // 1시간

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        // 사용자 등록 or 조회
        users user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new users(email, nickname)));

        // JWT 생성
        String token = jwtTokenProvider.createToken(user);

        // 프론트로 리디렉션
        response.sendRedirect("http://www.curi-o.site/login/success?token=" + token);
    }
}
