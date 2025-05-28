package team.backend.curio.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.client.KakaoOAuthClient;
import team.backend.curio.dto.authlogin.KakaoLoginRequestDto;
import team.backend.curio.dto.authlogin.KakaoLoginResponseDto;
import team.backend.curio.dto.authlogin.OAuthUserInfo;
import team.backend.curio.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KakaoOAuthClient kakaoOAuthClient;

    @PostMapping("/kakao/login")
    public ResponseEntity<KakaoLoginResponseDto> kakaoLogin(@RequestBody KakaoLoginRequestDto requestDto){
        String code = requestDto.getCode();
        KakaoLoginResponseDto result=authService.loginWithKakao(code);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/kakao/userinfo")
    public OAuthUserInfo getKakaoUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        // "Bearer {token}"에서 "Bearer " 제거
        String accessToken = authorizationHeader.replace("Bearer ", "").trim();
        return kakaoOAuthClient.getUserInfo(accessToken);
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
