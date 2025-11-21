package com.example.perkmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security Configuration
 * Provides password encoding using BCrypt
 */
@Configuration
public class SecurityConfig {

    /**
     * Password encoder bean using BCrypt hashing algorithm
     * BCrypt automatically handles salting and is resistant to rainbow table attacks
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
