package com.example.perkmanager.controller;

import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.repository.PerkRepository;
import com.example.perkmanager.repository.ProfileRepository;
import com.example.perkmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

@WebMvcTest(AppController.class)
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private PerkRepository perkRepo;

    @MockBean
    private ProfileRepository profileRepo;

    private AppUser testUser;
    private Perk testPerk;

    @BeforeEach
    void setUp() {
        testUser = new AppUser("test@example.com", "password");
        testPerk = new Perk(
                "Test Perk",
                null, // MembershipType
                null, // ProductType
                null, // Start date
                null, // End date
                testUser
        );
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userRepo.findAll()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/api/perkmanager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void testCreateUser() throws Exception {
        when(userRepo.save(any(AppUser.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/perkmanager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetUserPerks() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser)); // Ensure Optional.of(testUser)
        when(perkRepo.findByPostedBy(testUser)).thenReturn(Collections.singletonList(testPerk));

        mockMvc.perform(get("/api/perkmanager/1/perks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test Perk"));
    }

    @Test
    void testLoginUser() throws Exception {
        when(userRepo.findByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(post("/api/perkmanager/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testCreatePerkForUser() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(testUser));
        when(perkRepo.save(any(Perk.class))).thenReturn(testPerk);

        mockMvc.perform(post("/api/perkmanager/1/perks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Test Perk\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test Perk"));
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Mock the repository to return empty for ID 1
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/api/perkmanager/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Mock the repository to return empty for ID 1
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/perkmanager/1"))
                .andExpect(status().isNotFound());
    }
}