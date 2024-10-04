package com.example.back.service;

import com.example.back.DTO.SongDTO;
import com.example.back.DTO.albumDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Service
public class AlbumInfoService {
    private final String API_URL = "https://api.genius.com";
    private final String ACCESS_TOKEN; // Genius API 토큰
    private final WebClient webClient;

    public AlbumInfoService(@Value("${GENIUS_API_TOKEN}")String accessToken) {
        ACCESS_TOKEN = accessToken;
        this.webClient = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .build();
    }

    // 인기 있는 음악 get
    public Mono<List<albumDTO>> getPopularSongs() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", "popular") // 인기 음악을 검색
                        .queryParam("per_page", 10) // 최대 10개의 결과만 가져오기
                        .build())
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::convertToAlbumDTOList)
                .switchIfEmpty(Mono.empty());
    }

    // JSON 응답을 SongDto 리스트로 변환하는 메서드
    private Mono<List<albumDTO>> convertToAlbumDTOList(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode hits = root.path("response").path("hits");
            List<albumDTO> albumDTOS = new ArrayList<>();

            // "hits"가 배열일 경우 처리
            if (hits.isArray()) {
                for (JsonNode hit : hits) {
                    JsonNode songInfo = hit.path("result");
                    String title = songInfo.path("title").asText(); // 노래 제목
                    String artist = songInfo.path("primary_artist").path("name").asText(); // 가수 이름
                    String albumImageUrl = songInfo.path("song_art_image_url").asText(); // 앨범 이미지 URL

                    // albumDTOS 객체 생성 및 리스트에 추가
                    albumDTOS.add(new albumDTO(title, artist, albumImageUrl));
                }
            }
            return Mono.just(albumDTOS);

        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Failed to parse JSON response", e));
        }
    }

}
