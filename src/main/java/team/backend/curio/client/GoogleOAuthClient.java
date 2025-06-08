package team.backend.curio.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import team.backend.curio.dto.authlogin.OAuthUserInfo;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private final RestTemplate restTemplate;

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    // code -> access_token
    public String getAccessToken(String code, boolean isLocal) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 🔥 env=local 처리된 redirect_uri
        String finalRedirectUri = isLocal
                ? redirectUri + "?env=local"
                : redirectUri;

        System.out.println("🟡 [카카오 토큰 요청] redirect_uri = " + finalRedirectUri);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", finalRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("구글 토큰 요청 실패: status = " + response.getStatusCode());
        }

        return (String) response.getBody().get("access_token");
    }

    // 액세스토큰으로 사용자 정보 요청
    public OAuthUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(USERINFO_URL, HttpMethod.GET, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("구글 사용자 정보 요청 실패: " + response.getStatusCode());
        }

        Map<String, Object> body = response.getBody();

        String email = (String) body.get("email");
        String nickname = (String) body.get("name");  // name을 nickname으로 사용
        String picture = (String) body.get("picture");

        return new OAuthUserInfo(email, nickname, picture);
    }

    // ✅ Step 3: code → accessToken → 사용자 정보까지 한번에
    public OAuthUserInfo getUserInfoByCode(String code, boolean isLocal) {
        String accessToken = getAccessToken(code, isLocal);
        return getUserInfo(accessToken);
    }
}
