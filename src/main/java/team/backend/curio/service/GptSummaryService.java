package team.backend.curio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.backend.curio.dto.KeywordDto;
import team.backend.curio.dto.PopularKeywordDto;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GptSummaryService {

    @Value("${openai.api-key}")
    private String apiKey;

    public String summarize(String content, String type) {
        String prompt = switch (type) {
            case "short" -> "핵심 사건과 배경 정보를 포함해 3문장으로 요약해 주세요.";
            case "medium" -> "핵심 사건과 배경 정보를 포함해 7문장으로 요약해 주세요.";
            case "long" -> "핵심 사건과 배경 정보를 포함해 이 기사의 주제에 대해 관련지식이 없는 사람도 알아 들을 수 있을 정도의 글을 어조는 '-했다' 로 유지해서 정리해줘. 요약된 글의 분량도 원문이랑 비슷했음 좋겠어 ";
            default -> throw new IllegalArgumentException("지원하지 않는 요약 타입");
        };

        return callGptApi(prompt + "\n" + content);
    }

    public String callGptApi(String fullPrompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "temperature", 0,
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

    public List<KeywordDto> callGptForKeywordExtraction(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                entity,
                Map.class
        );

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        String gptResponseJson = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

        System.out.println("GPT 응답 원문: " + gptResponseJson);

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(gptResponseJson, new TypeReference<List<KeywordDto>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

}
