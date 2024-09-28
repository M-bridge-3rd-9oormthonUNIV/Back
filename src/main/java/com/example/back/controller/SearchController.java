package com.example.back.controller;

import com.example.back.DTO.SongDTO;
import com.example.back.model.SearchModel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SearchController {

    private final SearchModel searchModel;

    @Autowired
    public SearchController(SearchModel searchModel) {
        this.searchModel = searchModel;
    }

    @GetMapping("/api/search")
    public Mono<List<SongDTO>> getSearch(@RequestParam String artist, @RequestParam String track) {
        return searchModel.search(artist, track);
    }

    @GetMapping("/api/lyrics/original/{id}")
    public Mono<String> getOriginLyrics(@PathVariable("id") String songId) {
        return searchModel.getOriginLyrics(songId);
    }
}