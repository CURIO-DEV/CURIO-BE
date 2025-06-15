package team.backend.curio.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import team.backend.curio.dto.authlogin.OAuthUserInfo;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.admin-key}")
    private String adminKey;

    private final RestTemplate restTemplate;

    //카카오 인가코드'code'로 accesstoken 받기
    public String getAccessToken(String code,boolean isLocal) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        // 🔥 env=local 처리된 redirect_uriAdd commentMore actions
        String finalRedirectUri = redirectUri;

        System.out.println("🟡 [카카오 토큰 요청] redirect_uri = " + finalRedirectUri);

        //요청 파라미터 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", finalRedirectUri);
        params.add("code", code); //프론트에서 받아올 코드

        //요청후응답 받기(map으로 access-token만 추출)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        return (String) response.get("access_token");
    }

    //access token으로 사용자 정보 조회
    public OAuthUserInfo getUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        //bearear토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        //요청 후 응답 받기
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class).getBody();

        //⬇️로그 확인
        System.out.println("카카오 사용자 정보 응답: " + response);

        // ✅ 1. 카카오 고유 user_id 추출
        Long kakaoUserId = ((Number) response.get("id")).longValue();

        // ✅ [추가] 콘솔에 user_id 로그 출력
        System.out.println("🔍 카카오 사용자 ID: " + kakaoUserId);  // ← 이게 oauthId로 저장되는 값

        // Null 체크 추가 (중요!)
        if (response == null) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: 응답이 null입니다.");
        }

        //kakao 응답 데이터에서 원하는 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        if (kakaoAccount == null) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: kakao_account가 null입니다.");
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: profile이 null입니다.");
        }

        //dto로 변환하여 반환
        return OAuthUserInfo.builder()
                .email((String) kakaoAccount.get("email"))
                .nickname((String) profile.get("nickname"))
                .profileImage((String) profile.get("profile_image_url"))
                .oauthId(String.valueOf(kakaoUserId))
                .socialType(1)
                .build();
    }

    // code → 사용자 정보까지 한 번에 처리하는 메서드 추가 (callback용)
    public OAuthUserInfo getUserInfoByCode(String code,boolean isLocal) {
        // 1. code로 access_token 발급
        String accessToken = getAccessToken(code, isLocal);

        // 2. access_token으로 사용자 정보 조회
        return getUserInfo(accessToken);
    }

    // 카카오 연결 끊기
    public void unlink(String kakaoUserId) {
        String url = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + adminKey); // ✅ Admin Key 꼭 KakaoAK 붙이기

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", kakaoUserId); // ← 이 값은 Long 타입이어도 문자열로 보내면 됩니다

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("✅ [카카오 unlink] 성공 - userId: " + kakaoUserId);
        } catch (Exception e) {
            System.err.println("❌ [카카오 unlink] 실패 - userId: " + kakaoUserId);
            e.printStackTrace();
        }
    }
}
