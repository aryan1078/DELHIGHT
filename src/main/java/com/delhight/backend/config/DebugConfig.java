package com.delhight.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DebugConfig {

    @Value("${google.api.key:NOT_SET}")
    private String apiKey;

    @PostConstruct
    public void printKeyStatus() {
        if ("NOT_SET".equals(apiKey)) {
            System.out.println("❌ GOOGLE_API_KEY NOT FOUND");
        } else {
            System.out.println("✅ GOOGLE_API_KEY loaded successfully");
        }
    }
}
