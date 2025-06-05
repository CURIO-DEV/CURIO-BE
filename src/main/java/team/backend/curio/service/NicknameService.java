package team.backend.curio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.json.JSONObject;

@Service
@RequiredArgsConstructor
public class NicknameService {
    @Value("${openai.api-key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String generateNickname() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // GPT에게 보낼 메시지
        String prompt = "형용사 + 명사 형태의 자연스럽고 유쾌한 한글 닉네임 하나만 생성해줘. 예: 귀여운 고양이, 불타는 치즈볼";

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        JSONObject body = new JSONObject();
        body.put("model", "gpt-4o");
        body.put("messages", new org.json.JSONArray().put(message));
        body.put("temperature", 0.8); // 다양성 조절

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);
            JSONObject result = new JSONObject(response.getBody());
            return result
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();
        } catch (Exception e) {
            throw new RuntimeException("닉네임 생성 중 오류 발생: " + e.getMessage());
        }
    }
}
