package team.backend.curio.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team.backend.curio.domain.users;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${JWT_SECRET}")
    private String secretKey;

    private final long ACCESS_EXP = 1000L * 60 * 60; // 60분
    private final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7; // 7일

    // Access Token 생성
    public String createAccessToken(users user) {
        return createToken(user, ACCESS_EXP);
    }

    // Refresh Token 생성
    public String createRefreshToken(users user) {
        return createToken(user, REFRESH_EXP);
    }

    public String createToken(users user, long exp) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("userId", user.getUserId());
        claims.put("nickname", user.getNickname());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + exp))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public String createToken(users user) {
        return createToken(user, ACCESS_EXP); // 기본값: Access Token 만료시간
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }
}

