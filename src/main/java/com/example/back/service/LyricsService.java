package com.example.back.service;

import com.example.back.LyricsScraper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LyricsService {
    private final String API_URL = "https://api.genius.com";
    private final String ACCESS_TOKEN; // Genius API 토큰
    private final WebClient webClient;
    private final LyricsScraper lyricsScraper;
    private final LyricsCacheService lyricsCacheService;

    public LyricsService(LyricsScraper lyricsScraper, LyricsCacheService lyricsCacheService) {
        Dotenv dotenv = Dotenv.load();
        this.ACCESS_TOKEN = dotenv.get("GENIUS_API_TOKEN");

        this.webClient = WebClient.builder()
            .baseUrl(API_URL)
            .defaultHeader("Authorization", "Bearer " + ACCESS_TOKEN)
            .build();

        this.lyricsScraper = lyricsScraper;
        this.lyricsCacheService = lyricsCacheService;
    }

    //songId로 가사 URL을 가져오고, 가사 스크래핑 메소드를 호출
    //캐시에서 먼저 원본 가사를 찾은 후, 없으면 API 호출 후 캐시 저장
    public Mono<String> getOriginLyrics(String songId) {
        return lyricsCacheService.getCachedLyrics(songId)
            .switchIfEmpty(
                getLyricsUrl(songId)
                    .flatMap(lyricsUrl -> Mono.just(lyricsScraper.scrapeLyrics(lyricsUrl)))
                    .doOnNext(lyrics -> lyricsCacheService.cacheOriginalLyrics(songId, lyrics))
            );
    }

    //가사 가져오기
    public Mono<String> getLyricsUrl(String songId) {
        return webClient.get()
            .uri("/songs/{songId}", songId)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(response -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response);
                    String lyricsUrl = root.path("response").path("song").path("url").asText();
                    return Mono.just(lyricsUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Mono.error(new RuntimeException("Error parsing Genius API response"));
                }
            });
    }
}
