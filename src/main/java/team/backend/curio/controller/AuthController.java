package team.backend.curio.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.client.KakaoOAuthClient;
import team.backend.curio.client.GoogleOAuthClient;
import team.backend.curio.domain.users;
import team.backend.curio.dto.authlogin.SocialLoginResponseDto;
import team.backend.curio.dto.authlogin.OAuthUserInfo;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final GoogleOAuthClient googleOAuthClient;

    @Value("${frontend.redirect-url}")
    private String frontendRedirectUrl;
  
    @Operation(summary = "카카오 소셜로그인 callback")
    @GetMapping("/kakao/callback")
    public ResponseEntity<SocialLoginResponseDto> kakaoCallback(@RequestParam String code, HttpServletRequest request) {
        // 1. 받은 code로 access_token 요청
        String accessToken = kakaoOAuthClient.getAccessToken(code);

        // 2. access_token으로 사용자 정보 요청
        OAuthUserInfo userInfo = kakaoOAuthClient.getUserInfo(accessToken);

        // 3. 사용자 정보로 회원가입/로그인 처리
        SocialLoginResponseDto loginResponse = authService.loginWithKakao(userInfo);

        String referer = request.getHeader("Referer");
        boolean isLocal = referer != null && referer.contains("localhost");

        //Long userId = loginResponse.getUserId(); // DB 저장

        // 5. 환경에 따라 리다이렉트 경로 설정
        String baseRedirectUrl = isLocal ? "http://localhost:3000" : "https://curi-o.site";

        String redirectUrl = baseRedirectUrl + "?userId=" + loginResponse.getUserId()
                + "&nickname=" + URLEncoder.encode(loginResponse.getNickname(), StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(loginResponse.getEmail(), StandardCharsets.UTF_8);

        //로그인 처리(db저장) 후 클라이언트에 사용자 정보 응답
        return ResponseEntity.status(302)
                .header("Location",redirectUrl)
                .build();
    }

    @Operation(summary = "카카오로그인 사용자 정보 조회")
    @GetMapping("/kakao/userinfo")
    public ResponseEntity<List<users>> getKakaoUsers() {
        List<users> kakaoUsers = userRepository.findBySocialType(1);
        return ResponseEntity.ok(kakaoUsers);
    }

    @Operation(summary ="구글 소셜로그인 callback")
    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam String code, HttpServletRequest request) {
        // 1. 받은 code로 access_token 요청
        String accessToken = googleOAuthClient.getAccessToken(code);

        // 2. access_token으로 사용자 정보 요청
        OAuthUserInfo userInfo = googleOAuthClient.getUserInfo(accessToken);

        // 3. 사용자 정보로 회원가입/로그인 처리
        SocialLoginResponseDto loginResponse = authService.loginWithGoogle(userInfo);

        //Long userId = loginResponse.getUserId(); // DB 저장된 userId

        // 4. Host 또는 Referer로 로컬/배포 환경 판단
        String referer = request.getHeader("Referer"); // 또는 request.getHeader("Origin") 도 가능
        boolean isLocal = referer != null && referer.contains("localhost");

        // 환경에 따라 리다이렉트 주소 분기
        String baseRedirectUrl = isLocal ? "http://localhost:3000" : "https://curi-o.site";

        // 4. 프론트로 리다이렉트 URL 생성
        String redirectUrl = baseRedirectUrl + "?userId=" + loginResponse.getUserId()
                + "&nickname=" + URLEncoder.encode(loginResponse.getNickname(), StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(loginResponse.getEmail(), StandardCharsets.UTF_8); // [추가] 프론트 리다이렉트 URL 생성

        // 5. 302 리다이렉트 응답
        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    @Operation(summary = "카카오로그인 사용자 정보 조회")
    @GetMapping("/google/userinfo")
    public ResponseEntity<List<users>> getGoogleUsers() {
        List<users> googleUsers = userRepository.findBySocialType(2);
        return ResponseEntity.ok(googleUsers);
    }
}
