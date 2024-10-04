package com.example.back.controller;

import com.example.back.DTO.albumDTO;
import com.example.back.service.AlbumInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/api")
public class AlbumInfoController {

    private final AlbumInfoService albumInfoService;


    public AlbumInfoController(AlbumInfoService albumInfoService) {
        this.albumInfoService = albumInfoService;
    }

    @GetMapping("/popular_albums")
    public Mono<ResponseEntity<List<albumDTO>>> getPopularAlbum() {
        return albumInfoService.getPopularSongs()
                .flatMap(results -> {
                    return Mono.just(ResponseEntity.ok(results)); // 결과가 있으면 200 OK와 함께 리스트 반환
                })
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));   // 결과가 없으면 204 No Content 반환
    }
}
