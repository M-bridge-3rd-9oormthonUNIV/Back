package com.example.back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = {"chatCache"})
public class CacheService {
    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // 새로운 cacheId 생성
    public String createCacheId() {
        return UUID.randomUUID().toString();  // 고유한 cacheId 생성
    }

    // 대화 저장
    public void saveConversation(String cacheId, List<Map<String, String>> messages) {
        Cache cache = cacheManager.getCache("ChatCache");
        if(cache != null) {
            cache.put(cacheId, messages);
        }
    }

    // 대화 가져오기
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getConversation(String cacheId) {
        Cache cache = cacheManager.getCache("ChatCache");
        if (cache != null) {
            List<Map<String, String>> conversation = cache.get(cacheId, List.class);
            return conversation != null ? conversation : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    // 특정 cacheId를 삭제할 수도 있음
    public void clearCache(String cacheId) {
        cacheManager.getCache("chatCache").evict(cacheId);
    }
}
