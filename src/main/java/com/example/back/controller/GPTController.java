package com.example.back.controller;

import com.example.back.service.GPTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class GPTController {
    private final GPTService gptService;

    @Autowired
    public GPTController(GPTService gptService) {
        this.gptService = gptService;
    }

    @PostMapping("")
    public ResponseEntity<?> getAssistantMsg(@RequestBody Map<String, String> requestBody) throws JsonProcessingException{
        String response = requestBody.get("msg");
        return gptService.getAssistantMsg(response);
    }
}
