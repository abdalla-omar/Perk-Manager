package com.example.perkmanager.model;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    @Test
    void testAppUserCreation() {
        AppUser user = new AppUser("test@example.com", "password");
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertNotNull(user.getProfile());
    }
}