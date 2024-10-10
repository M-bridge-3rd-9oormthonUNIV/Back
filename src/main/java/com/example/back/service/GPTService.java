package com.example.back.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GPTService {
    private final String ACCESS_TOKEN;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CacheService cacheService;

    public GPTService(@Value("${OPEN_API_TOKEN}")String accessToken, CacheService cacheService) {
        ACCESS_TOKEN = accessToken;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.cacheService = cacheService;
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

    public String getChatResponse(String cacheId, String userMessage) throws JsonProcessingException {
        List<Map<String, String>> chatList = cacheService.getConversation(cacheId);
        // 캐시 ID가 없으면 새로운 대화 시작
        if (cacheId == null || cacheId.isEmpty()) {
            chatList = new ArrayList<>();
        }
        Map<String, String> userMsgMap = new HashMap<>();
        userMsgMap.put("role", "user");
        userMsgMap.put("content", userMessage);
        chatList.add(userMsgMap);

        // ChatGPT에게 대화 내용을 전달
        String requestBody = createRequestBody(chatList);
        JsonNode jsonNode = sendRequestToGPT(requestBody);

        // 대화 응답을 기록
        String gptResponse = jsonNode.path("choices").get(0).path("message").path("content").asText();
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("role", "assistant");
        responseMap.put("content", gptResponse);
        chatList.add(responseMap);

        // 대화 기록을 캐시에 저장
        cacheService.saveConversation(cacheId, chatList);

        return gptResponse;
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
