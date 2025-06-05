package team.backend.curio.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.client.KakaoOAuthClient;
import team.backend.curio.client.GoogleOAuthClient;
import team.backend.curio.domain.users;
import team.backend.curio.dto.UserDTO.TokenResponse;
import team.backend.curio.dto.authlogin.SocialLoginResponseDto;
import team.backend.curio.dto.authlogin.OAuthUserInfo;
// import team.backend.curio.jwt.JwtTokenProvider;
import team.backend.curio.jwt.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @Value("${frontend.redirect-url}")
    private String frontendRedirectUrl;
  
    /*@Operation(summary = "카카오 소셜로그인 callback")
    @GetMapping("/kakao/callback")
    public ResponseEntity<SocialLoginResponseDto> kakaoCallback(@RequestParam String code, HttpServletRequest request) {
        // 1. 받은 code로 access_token 요청
        String accessToken = kakaoOAuthClient.getAccessToken(code);

        // 2. access_token으로 사용자 정보 요청
        OAuthUserInfo userInfo = kakaoOAuthClient.getUserInfo(accessToken);

        users user = authService.findOrCreateKakaoUser(userInfo); // users 엔티티 리턴하도록 수정

        String token = jwtUtil.createToken(user);

        // 수정: Referer 대신 serverName과 port 사용
        String serverName = request.getServerName(); // ex: localhost, curi-o.site
        int port = request.getServerPort();          // ex: 8080
        boolean isLocal = serverName.equals("localhost") && port == 8080;

        // 5. 환경에 따라 리다이렉트 경로 설정
        String baseRedirectUrl = isLocal ? "http://localhost:3000" : "https://curi-o.site";
        String redirectUrl = baseRedirectUrl + "?token=" + token;

        //로그인 처리(db저장) 후 클라이언트에 사용자 정보 응답
        return ResponseEntity.status(302)
                .header("Location",redirectUrl)
                .build();
    }*/

    @Operation(summary = "카카오 소셜로그인 callback - JSON 응답")
    @GetMapping("/kakao/callback")
    public ResponseEntity<TokenResponse> kakaoCallback(@RequestParam String code) {
        String accessToken = kakaoOAuthClient.getAccessToken(code);
        OAuthUserInfo userInfo = kakaoOAuthClient.getUserInfo(accessToken);

        users user = authService.findOrCreateKakaoUser(userInfo);

        // Access & Refresh Token 생성
        String accessJwt = jwtUtil.createAccessToken(user);
        String refreshJwt = jwtUtil.createRefreshToken(user);

        TokenResponse tokenResponse = new TokenResponse(accessJwt, refreshJwt);
        return ResponseEntity.ok(tokenResponse); // JSON 응답
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

        users user = authService.findOrCreateGoogleUser(userInfo);

        String token = jwtUtil.createToken(user);

        // 수정: Referer 대신 serverName과 port 사용
        String serverName = request.getServerName(); // ex: localhost, curi-o.site
        int port = request.getServerPort();          // ex: 8080
        boolean isLocal = serverName.equals("localhost") && port == 8080;

        // 환경에 따라 리다이렉트 주소 분기 , 프론트로 리다이렉트 URL 생성
        String baseRedirectUrl = isLocal ? "http://localhost:3000" : "https://curi-o.site";
        String redirectUrl = baseRedirectUrl + "?token=" + token;

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
