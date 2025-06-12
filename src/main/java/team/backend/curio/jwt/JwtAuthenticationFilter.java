package team.backend.curio.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import team.backend.curio.domain.users;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.security.CustomUserDetails;
import jakarta.servlet.http.Cookie;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.startsWith("/auth") || path.startsWith("/swagger")) {
            return true;
        }

        // "/curio/api/users/interests/"로 시작하고 "/news"로 끝나는 경로 필터 제외
        if (path.startsWith("/curio/api/users/interests/") && path.endsWith("/news")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.getEmail(token);
            users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자 없음"));

            //로그 찍기
            log.info("✅ JWT 토큰 이메일: {}", email);
            log.info("✅ DB에서 찾은 유저: {}", user.getEmail());
            log.info("✅ ROLE_USER 권한 부여됨");

            CustomUserDetails userDetails = new CustomUserDetails(user);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            /*
            CustomUserDetails userDetails = new CustomUserDetails(user);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
*/
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        System.out.println("👉 요청 Origin: " + request.getHeader("Origin"));
        System.out.println("👉 요청 쿠키: " + Arrays.toString(cookies));

        if (cookies == null) {
            System.out.println("🪙 [resolveToken] 쿠키 없음 (null)");
            return null;
        }

        System.out.println("🪙 [resolveToken] 쿠키 수: " + cookies.length);
        for (Cookie cookie : cookies) {

            System.out.println("🍪 쿠키 이름: " + cookie.getName() + ", 값: " + cookie.getValue());
            if ("accessToken".equals(cookie.getName())) {
                System.out.println("✅ accessToken 쿠키 발견!");
                return cookie.getValue();
            }
        }
        System.out.println("❌ accessToken 쿠키 없음");
        return null;
    }

}
