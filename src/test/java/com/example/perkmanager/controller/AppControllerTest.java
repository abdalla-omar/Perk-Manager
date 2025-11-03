package com.example.perkmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AppControllerTest {

    @Autowired
    private AppController appController;

    @Test
    void testHealth() {
        String response = appController.health();
        assertEquals("Application is running", response);
    }

    @Test
    void testHello() {
        String response = appController.hello();
        assertEquals("Hello from Perk Manager!", response);
    }
}