package com.example.back.controller;

import com.example.back.service.YoutubeVideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class YoutubeVideoController {
    private final YoutubeVideoService youtubeVideoService;

    public YoutubeVideoController(YoutubeVideoService youtubeVideoService) {
        this.youtubeVideoService = youtubeVideoService;
    }

    @GetMapping("/api/youtube/video")
    public Mono<ResponseEntity<String>> searchVideo(@RequestParam String artist, @RequestParam String song) {
        // 노래 제목과 아티스트를 하나의 검색어(query)로 구성
        String query = song + " " + artist;

        return youtubeVideoService.searchVideo(query)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
