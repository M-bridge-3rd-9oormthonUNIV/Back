package com.example.back.controller;

import com.example.back.service.GPTService;
import com.example.back.service.LyricsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/lyrics")
public class LyricsController {
    private final LyricsService lyricsService;
    private final GPTService gptService;

    @Autowired
    public LyricsController(LyricsService lyricsService, GPTService gptService) {
        this.lyricsService = lyricsService;
        this.gptService = gptService;
    }

    @GetMapping("/original/{songId}")
    public Mono<String> getOriginLyrics(@PathVariable("songId") String songId) {
        return lyricsService.getOriginLyrics(songId);
    }

    @PostMapping("/translate/{songId}/{lang}")
    public Mono<ResponseEntity<String>> getTranslateLyrics(@PathVariable("songId") String songId, @PathVariable String lang) {
        return lyricsService.getOriginLyrics(songId)
            .flatMap(originalLyrics -> {
                try {
                    JsonNode translationResponse = gptService.getTranslation(originalLyrics, lang);
                    String translatedLyrics = translationResponse.path("choices").get(0).path("message").path("content").asText();
                    return Mono.just(ResponseEntity.ok(translatedLyrics));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(500).body("Translation failed: " + e.getMessage()));
                }
            })
            .switchIfEmpty(Mono.just(ResponseEntity.status(404).body("Original lyrics not found")));
    }
}
