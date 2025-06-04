package team.backend.curio.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import team.backend.curio.domain.users;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;



@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${JWT_SECRET}")
    private String secretKey;

    private final long validityInMilliseconds = 3600000; // 1시간

    // 1. 토큰 생성
    public String createToken(users user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail()); // 토큰 제목(sub)에 이메일 저장
        claims.put("userId", user.getUserId());
        claims.put("nickname", user.getNickname());

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds); // 1시간 후 만료

        return Jwts.builder()
                .setClaims(claims) // 클레임(유저정보)
                .setIssuedAt(now) // 토큰 발급시간
                .setExpiration(expiry) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) // 시크릿 키로 서명
                .compact(); // 최종적으로 문자열 토큰 반환
    }

    // 2. 토큰 기반으로 인증 객체 생성
    public Authentication getAuthentication(String token) {
        String email = getEmail(token); // 토큰에서 이메일 파싱
        return new UsernamePasswordAuthenticationToken(email, "", List.of()); // 인증 객체 생성 (권한은 없음)
    }

    public String getEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes()) // 서명 검증을 위한 키 설정
                .parseClaimsJws(token) // 토큰 파싱
                .getBody()
                .getSubject(); // subject = 이메일
    }

    // 4. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> parsedClaims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token); // 파싱 시 유효성/서명 자동 체크

            return !parsedClaims.getBody().getExpiration().before(new Date()); // 만료되지 않았는지 확인
        } catch (Exception e) {
            return false; // 파싱 에러, 서명 불일치, 만료 등 → false
        }
    }
}

