package team.backend.curio.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.client.KakaoOAuthClient;
import team.backend.curio.domain.users;
import team.backend.curio.dto.authlogin.KakaoLoginRequestDto;
import team.backend.curio.dto.authlogin.KakaoLoginResponseDto;
import team.backend.curio.dto.authlogin.OAuthUserInfo;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.service.AuthService;
import java.util.List;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final KakaoOAuthClient kakaoOAuthClient;

    @Operation(summary = "카카오 소셜로그인 callback")
    @GetMapping("/kakao/callback")
    public ResponseEntity<KakaoLoginResponseDto> kakaoCallback(@RequestParam String code) {
        // 1. 받은 code로 access_token 요청
        String accessToken = kakaoOAuthClient.getAccessToken(code);

        // 2. access_token으로 사용자 정보 요청
        OAuthUserInfo userInfo = kakaoOAuthClient.getUserInfo(accessToken);

        // 3. 사용자 정보로 회원가입/로그인 처리
        KakaoLoginResponseDto loginResponse = authService.loginWithKakao(userInfo);
        Long userId = loginResponse.getUserId(); // DB 저장

        //로그인 처리(db저장) 후 클라이언트에 사용자 정보 응답
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "카카오로그인 사용자 정보 조회")
    @GetMapping("/kakao/userinfo")
    public ResponseEntity<List<users>> getKakaoUsers() {
        List<users> kakaoUsers = userRepository.findBySocialType(1);
        return ResponseEntity.ok(kakaoUsers);
    }

    /*
    @PostMapping("/login/google")
    public ResponseEntity<String> googleLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String result = authService.loginWithGoogle(code);
        return ResponseEntity.ok(result);
    }
    */
}
