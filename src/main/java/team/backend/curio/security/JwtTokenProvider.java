package team.backend.curio.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team.backend.curio.domain.users;

import java.util.Date;
@Component
public class JwtTokenProvider {

    private final String secretKey;

    //환경변수에서 키 주입
    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    // JWT 발급 메서드
    public String createToken(users user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("nickname", user.getNickname())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String resolveToken(jakarta.servlet.http.HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getSubject();
    }
}
