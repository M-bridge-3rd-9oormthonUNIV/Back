package com.example.back;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testHelloWorld {

    @GetMapping("/hello")
    public String hello(){
        return "Hello World!";
    }
}
