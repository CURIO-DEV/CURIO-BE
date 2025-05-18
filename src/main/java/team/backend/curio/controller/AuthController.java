package team.backend.curio.controller;

import team.backend.curio.security.JwtTokenProvider;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.domain.users;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/kakao/userinfo")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.badRequest().body("로그인 정보가 없습니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        users user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자 없음");
        }

        return ResponseEntity.ok(user);
    }
}
