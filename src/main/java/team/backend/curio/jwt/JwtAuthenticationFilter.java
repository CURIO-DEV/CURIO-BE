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
        return path.startsWith("/auth") || path.startsWith("/swagger");
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

            // 여기에서 ROLE_USER 권한을 명시적으로 부여
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user, // Principal
                    null, // Credentials
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
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
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
