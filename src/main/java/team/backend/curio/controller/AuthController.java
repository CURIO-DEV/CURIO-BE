package team.backend.curio.controller;


import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<SocialLoginResponseDto> kakaoCallback(@RequestParam String code) {
        // 1. 받은 code로 access_token 요청
        String accessToken = kakaoOAuthClient.getAccessToken(code);

        // 2. access_token으로 사용자 정보 요청
        OAuthUserInfo userInfo = kakaoOAuthClient.getUserInfo(accessToken);

        // 3. 사용자 정보로 회원가입/로그인 처리
        SocialLoginResponseDto loginResponse = authService.loginWithKakao(userInfo);
        Long userId = loginResponse.getUserId(); // DB 저장

        String redirectUrl = frontendRedirectUrl + "?userId=" + userId
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
    public ResponseEntity<Void> googleCallback(@RequestParam String code) {
        // 1. 받은 code로 access_token 요청
        String accessToken = googleOAuthClient.getAccessToken(code);

        // 2. access_token으로 사용자 정보 요청
        OAuthUserInfo userInfo = googleOAuthClient.getUserInfo(accessToken);

        // 3. 사용자 정보로 회원가입/로그인 처리
        SocialLoginResponseDto loginResponse = authService.loginWithGoogle(userInfo);

        Long userId = loginResponse.getUserId(); // DB 저장된 userId

        String nickname = loginResponse.getNickname() != null ? loginResponse.getNickname() : "";
        String email = loginResponse.getEmail() != null ? loginResponse.getEmail() : "";

        // 4. 프론트로 리다이렉트 URL 생성
        String redirectUrl = frontendRedirectUrl + "?userId=" + userId
                + "&nickname=" + URLEncoder.encode(nickname, StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8); // [추가] 프론트 리다이렉트 URL 생성

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
