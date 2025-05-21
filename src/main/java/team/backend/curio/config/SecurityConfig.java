package team.backend.curio.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import team.backend.curio.security.CustomOAuth2SuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.backend.curio.security.JwtAuthenticationFilter;
import team.backend.curio.service.CustomOAuth2UserService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/auth/kakao/userinfo",
                                "/auth/google/userinfo",
                                "/articles/**",
                                "/search").permitAll()  // 모든 경로 허용
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 구글 사용자 정보 처리용 서비스 등록
                        )
                        .successHandler(customOAuth2SuccessHandler) // 카카오/구글 둘 다 로그인 성공 시 처리
                );

        return http.build();
    }
}
