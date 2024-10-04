package com.example.back.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LyricsCacheService {
    private final Map<String, String> lyricsCache = new HashMap<>();

    // 원본 가사를 캐시에 저장
    public void cacheOriginalLyrics(String songId, String lyrics) {
        lyricsCache.put(songId, lyrics);
    }

    // 캐시에서 원본 가사 조회
    public Mono<String> getCachedLyrics(String songId) {
        if (lyricsCache.containsKey(songId)) {
            return Mono.just(lyricsCache.get(songId));
        } else {
            return Mono.empty();
        }
    }

}
