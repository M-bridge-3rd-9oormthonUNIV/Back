package com.example.back.controller;

import com.example.back.service.LyricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LyricsController {

    private final LyricsService lyricsService;

    @Autowired
    public LyricsController(LyricsService lyricsService) {
        this.lyricsService = lyricsService;
    }

    @GetMapping("/api/lyrics/original/{songId}")
    public Mono<String> getOriginLyrics(@PathVariable("songId") String songId) {
        return lyricsService.getOriginLyrics(songId);
    }

}
