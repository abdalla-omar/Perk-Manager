package com.example.perkmanager.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppController {

    @GetMapping("/health")
    public String health() {
        return "Application is running";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Perk Manager!";
    }
}