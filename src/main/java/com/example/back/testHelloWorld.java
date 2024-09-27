package com.example.back;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testHelloWorld {

    // health check api
    @GetMapping("/")
    public String hello(){
        return "Hello World!";
    }
}
