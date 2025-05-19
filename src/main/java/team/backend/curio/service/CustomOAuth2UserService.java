package team.backend.curio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import team.backend.curio.domain.users;
import team.backend.curio.repository.UserRepository;
import java.time.LocalDateTime;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글 사용자 정보 파싱
        String email = oAuth2User.getAttribute("email");
        String nickname = oAuth2User.getAttribute("name");
        String profileImageUrl = oAuth2User.getAttribute("picture");

        // DB에 사용자 저장 or 조회
        users user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        users.builder()
                                .email(email)
                                .nickname(nickname)
                                .profile_image_url(profileImageUrl)
                                .socialType(2)
                                .newsletterEmail(email)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .summaryPreference(2)
                                .newsletterStatus(0)
                                .build()
                ));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "sub" // 구글의 사용자 고유 ID key
        );
    }
}
