package com.example.back.controller;

import com.example.back.DTO.SongDTO;
import com.example.back.model.SearchModel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/search")
    public Mono<List<SongDTO>> getTrackDetails(@RequestParam String artist, @RequestParam String track) {
        return searchModel.searchTrack(artist, track);
    }
}