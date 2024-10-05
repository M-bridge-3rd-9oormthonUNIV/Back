package com.example.back.controller;

import com.example.back.DTO.SongDTO;
import com.example.back.service.SearchService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    public Mono<ResponseEntity<List<SongDTO>>> getSearch(@RequestParam String artist, @RequestParam String song) {
        return searchService.search(artist, song)
            .flatMap(results -> {
                return Mono.just(ResponseEntity.ok(results)); // 결과가 있으면 200 OK와 함께 리스트 반환
            })
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));   // 결과가 없으면 204 No Content 반환
    }
}