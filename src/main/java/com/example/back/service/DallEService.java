package com.example.back.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Service
@Transactional
public class DallEService {

    private final String API_KEY;

    @Autowired
    public DallEService(@Value("${DALLE_API_KEY}")String apiKey) {
        API_KEY = apiKey;
    }

    // 이미지 생성
    public String generateImage(String prompt) {
        String DALLE_API_URL = "https://api.openai.com/v1/images/generations";
        // Time Out 해결
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Gson gson = new Gson();

        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("model", "dall-e-3"); // DallE 모델
        requestBodyJson.addProperty("prompt", prompt); // 사용자가 입력한 프롬프트 전달
        requestBodyJson.addProperty("size", "1024x1024"); // 이미지 크기 설정
        requestBodyJson.addProperty("n", 1); // 하나의 이미지만 생성

        RequestBody requestBody = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(DALLE_API_URL)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                return jsonResponse.getAsJsonArray("data").get(0).getAsJsonObject().get("url").getAsString();
            } else {
                assert response.body() != null;
                System.out.println("OpenAI API request failed with response: " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
