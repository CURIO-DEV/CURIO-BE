package team.backend.curio.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import jakarta.servlet.http.Cookie;
import java.io.IOException;
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

    /*@Operation(summary = "카카오 소셜로그인 callback - 쿠키방식")
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
    */
    @Operation(summary = "카카오 소셜로그인 callback - 쿠키 방식")
    @GetMapping("/kakao/callback")
    public void kakaoCallback(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = kakaoOAuthClient.getAccessToken(code);
        OAuthUserInfo userInfo = kakaoOAuthClient.getUserInfo(accessToken);
        users user = authService.findOrCreateKakaoUser(userInfo);

        String accessJwt = jwtUtil.createAccessToken(user);
        String refreshJwt = jwtUtil.createRefreshToken(user);

        // access token 쿠키
        Cookie accessCookie = new Cookie("accessToken", accessJwt);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true); // HTTPS 환경
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60); // 60분

        // refresh token 쿠키
        Cookie refreshCookie = new Cookie("refreshToken", refreshJwt);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

        // 쿠키 추가
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // 환경에 따른 리다이렉트 URL 설정
        String serverName = request.getServerName();
        int port = request.getServerPort();
        boolean isLocal = serverName.equals("localhost") && port == 8080;
        String redirectUrl = isLocal ? "http://localhost:3000" : "https://curi-o.site";

        // 리다이렉트
        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "카카오로그인 사용자 정보 조회")
    @GetMapping("/kakao/userinfo")
    public ResponseEntity<List<users>> getKakaoUsers() {
        List<users> kakaoUsers = userRepository.findBySocialType(1);
        return ResponseEntity.ok(kakaoUsers);
    }

    /*
    @Operation(summary ="구글 소셜로그인 callback")
    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam String code, HttpServletRequest request) {
        String accessToken = googleOAuthClient.getAccessToken(code);
        OAuthUserInfo userInfo = googleOAuthClient.getUserInfo(accessToken);

        users user = authService.findOrCreateGoogleUser(userInfo);

        // Access & Refresh Token 생성
        String accessJwt = jwtUtil.createAccessToken(user);
        String refreshJwt = jwtUtil.createRefreshToken(user);

        TokenResponse tokenResponse = new TokenResponse(accessJwt, refreshJwt);
        return ResponseEntity.ok(tokenResponse); // JSON 응답
    }
    */

    @Operation(summary = "구글 소셜로그인 callback - 쿠키 + 리다이렉트")
    @GetMapping("/google/callback")
    public void googleCallback(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 구글에서 accessToken 받아오기
        String accessToken = googleOAuthClient.getAccessToken(code);

        // 2. 사용자 정보 조회
        OAuthUserInfo userInfo = googleOAuthClient.getUserInfo(accessToken);

        // 3. 사용자 생성 or 조회
        users user = authService.findOrCreateGoogleUser(userInfo);

        // 4. JWT 토큰 생성
        String accessJwt = jwtUtil.createAccessToken(user);
        String refreshJwt = jwtUtil.createRefreshToken(user);

        // 5. 쿠키 설정
        Cookie accessCookie = new Cookie("accessToken", accessJwt);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60); // 1시간

        Cookie refreshCookie = new Cookie("refreshToken", refreshJwt);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // 6. 리다이렉트 경로 설정 (로컬 or 배포)
        String serverName = request.getServerName();
        int port = request.getServerPort();
        boolean isLocal = serverName.equals("localhost") && port == 8080;
        String redirectUrl = isLocal ? "http://localhost:3000" : "https://curi-o.site";


        // 7. 리다이렉트 응답
        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "구글로그인 사용자 정보 조회")
    @GetMapping("/google/userinfo")
    public ResponseEntity<List<users>> getGoogleUsers() {
        List<users> googleUsers = userRepository.findBySocialType(2);
        return ResponseEntity.ok(googleUsers);
    }
}
