package com.example.back.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GPTService {
    private final String ACCESS_TOKEN;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GPTService(@Value("${OPEN_API_TOKEN}")String accessToken) {
        ACCESS_TOKEN = accessToken;

        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ACCESS_TOKEN);
        return headers;
    }

    private String createRequestBody(List<Map<String, String>> messages) throws JsonProcessingException {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", "gpt-3.5-turbo");
        bodyMap.put("messages", messages);
        return objectMapper.writeValueAsString(bodyMap);
    }

    private JsonNode sendRequestToGPT(String body) throws JsonProcessingException {
        final String url = "https://api.openai.com/v1/chat/completions";
        HttpEntity<String> request = new HttpEntity<>(body, createHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return objectMapper.readTree(response.getBody());
    }

    public ResponseEntity<?> callChatGpt(String userMsg) throws JsonProcessingException {
        // 메시지 생성
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMsg);
        String requestBody = createRequestBody(List.of(message));

        // API 호출 및 응답 처리
        JsonNode jsonNode = sendRequestToGPT(requestBody);
        String content = jsonNode.path("choices").get(0).path("message").path("content").asText();
        return ResponseEntity.status(HttpStatus.OK).body(content);
    }

    public JsonNode getTranslation(String originalLyrics, String lang) throws JsonProcessingException {
        // 번역을 위한 프롬프트 메시지 생성
        String prompt = "Translate the following lyrics to lang: " + lang + ". while keeping the HTML tags intact. Return the translation in the same HTML format." + originalLyrics;

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        String requestBody = createRequestBody(List.of(message));

        // API 호출 및 응답 처리
        return sendRequestToGPT(requestBody);
    }
}
