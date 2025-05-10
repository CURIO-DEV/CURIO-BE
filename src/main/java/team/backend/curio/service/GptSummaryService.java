package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GptSummaryService {

    @Value("${openai.api-key}")
    private String apiKey;

    public String summarize(String content, String type) {
        String prompt = switch (type) {
            case "short" -> "다음 뉴스를 1문장으로 요약해줘.";
            case "medium" -> "다음 뉴스를 3문장으로 요약해줘.";
            case "long" -> "다음 뉴스를 5문장으로 요약해줘.";
            default -> throw new IllegalArgumentException("지원하지 않는 요약 타입");
        };

        return callGptApi(prompt + "\n" + content);
    }

    private String callGptApi(String fullPrompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "user", "content", fullPrompt)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                entity,
                Map.class
        );

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
    }

}
