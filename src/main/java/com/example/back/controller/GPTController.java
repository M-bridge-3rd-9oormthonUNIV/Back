package com.example.back.controller;

import com.example.back.service.CacheService;
import com.example.back.service.GPTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GPTController {

    private final GPTService gptService;
    private final CacheService cacheService;

    @Autowired
    public GPTController(GPTService gptService, CacheService cacheService) {
        this.gptService = gptService;
        this.cacheService = cacheService;
    }

    @PostMapping("/api/chat")
    public ResponseEntity<?> getResponseMsg(@RequestBody Map<String, String> requestBody) throws JsonProcessingException{
        String cacheId = requestBody.get("id");
        String userMsg = requestBody.get("message");

        //첫 요청인 경우 cacheId 생성
        if(cacheId == null || cacheId.isEmpty()){
            cacheId = cacheService.createCacheId();
        }

        String chatResponse = gptService.getChatResponse(cacheId, userMsg);
        Map<String, Object> map = new HashMap<>();
        map.put("id", cacheId);
        map.put("response", chatResponse);
        return ResponseEntity.ok(map);
    }
}
