package team.backend.curio.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    // 인증 예외 처리할 URL 목록 (Swagger, Auth 등)
    private static final List<String> EXCLUDE_URLS = List.of(
            "/trends/interests/keywords",
            "/trends/keywords",
            "/trends/popular-articles",
            "/search",
            "/articles/", // 시작 경로만 잡고 아래에서 상세 조건 처리
            "/swagger-ui.html", "/swagger-ui/**",
            "/v3/api-docs/**",
            "/auth/**",
            "/auth/kakao/callback",
            "/auth/google/callback"
    );

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        boolean match = EXCLUDE_URLS.stream().anyMatch(p -> pathMatcher.match(p, path));
        boolean isArticleSummary = pathMatcher.match("/articles/*/summary", path);
        boolean isArticleHeadline = pathMatcher.match("/articles/*/headline", path);

        return match || isArticleSummary || isArticleHeadline;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
