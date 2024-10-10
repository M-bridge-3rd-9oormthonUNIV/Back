package com.example.back.controller;

import com.example.back.service.DallEService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DallEController {
    private final DallEService dallEService;

    public DallEController(DallEService dallEService) {
        this.dallEService = dallEService;
    }

    // 이미지 생성하기
    @PostMapping("/image/generate")
    public ResponseEntity<?> generateImage(@RequestBody String prompt) {
        String imageUrl = dallEService.generateImage(prompt);
        return ResponseEntity.ok(imageUrl);
    }


}
