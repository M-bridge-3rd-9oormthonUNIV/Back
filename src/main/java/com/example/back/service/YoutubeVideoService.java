package com.example.back.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class YoutubeVideoService {
    private final String API_KEY;
    private final String API_URL = "https://www.googleapis.com/youtube/v3";
    private final WebClient webClient;

    public YoutubeVideoService(@Value("${YOUTUBE_API_KEY}")String accessToken) {
        API_KEY = accessToken;
        this.webClient = WebClient.builder()
                .baseUrl(API_URL)
                .build();
    }

    // 특정 노래 이름과 아티스트로 YouTube 비디오 하나를 검색하는 메서드
    public Mono<String> searchVideo(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("part", "snippet")
                        .queryParam("q", query) // 검색어 설정
                        .queryParam("type", "video")
                        .queryParam("maxResults", 1) // 최대 하나의 결과만 가져오기
                        .queryParam("key", API_KEY) // API 키 추가
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::extractVideoId); // JSON 응답에서 비디오 ID 추출
    }

    // JSON 응답에서 비디오 ID 추출
    private Mono<String> extractVideoId(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode items = root.path("items");

            if (items.isArray() && items.size() > 0) {
                String videoId = items.get(0).path("id").path("videoId").asText();
                return Mono.just(videoId);
            }

            return Mono.empty(); // 검색 결과가 없을 경우
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to parse YouTube API response", e));
        }
    }

}
