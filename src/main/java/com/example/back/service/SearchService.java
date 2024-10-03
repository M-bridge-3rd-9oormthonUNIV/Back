package com.example.back.service;

import com.example.back.LyricsScraper;
import io.github.cdimascio.dotenv.Dotenv;
import com.example.back.DTO.SongDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private final String API_URL = "https://api.genius.com";
    private final String ACCESS_TOKEN; // Genius API 토큰
    private final WebClient webClient;
    private final LyricsScraper lyricsScraper;

    public SearchService(LyricsScraper lyricsScraper) {
        Dotenv dotenv = Dotenv.load();
        this.ACCESS_TOKEN = dotenv.get("GENIUS_API_TOKEN");

        this.webClient = WebClient.builder()
            .baseUrl(API_URL)
            .defaultHeader("Authorization", "Bearer " + ACCESS_TOKEN)
            .build();

        this.lyricsScraper = lyricsScraper;
    }

    //검색 결과 반환
    public Mono<List<SongDTO>> search(String artist, String track) {
        String searchQuery = track + " " + artist;

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/search")
                .queryParam("q", searchQuery)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(this::parseSearchResponse)
            .switchIfEmpty(Mono.empty());
    }

    //검색 결과에서 정보 파싱
    private Mono<List<SongDTO>> parseSearchResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode hits = root.path("response").path("hits");
            List<SongDTO> songList = new ArrayList<>();

            if (hits.isArray() && hits.size() > 0) {
                JsonNode songInfo = hits.get(0).path("result");
                String title = songInfo.path("title").asText();
                String artist = songInfo.path("primary_artist").path("name").asText();
                String songId = songInfo.path("id").asText();

                SongDTO songDTO = new SongDTO(title, artist, songId);
                songList.add(songDTO);

                return Mono.just(songList);
            } else {
                return Mono.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(new RuntimeException("Error parsing Genius API response"));
        }
    }

    //songId로 가사 URL을 가져오고, 가사 스크래핑 메소드를 호출
    public Mono<String> getOriginLyrics(String songId) {
        return getLyricsUrl(songId)
            .flatMap(lyricsUrl -> Mono.just(lyricsScraper.scrapeLyrics(lyricsUrl)));
    }

    //가사 가져오기
    public Mono<String> getLyricsUrl(String songId) {
        return webClient.get()
            .uri("/songs/{id}", songId)
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

