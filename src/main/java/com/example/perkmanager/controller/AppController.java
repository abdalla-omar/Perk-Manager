package com.example.perkmanager.controller;

import com.example.perkmanager.repository.PerkRepository;
import com.example.perkmanager.repository.UserRepository;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/perkmanager")
public class AppController {

    @Autowired
    private PerkRepository perkRepo;
    @Autowired
    private UserRepository userRepo;

    @GetMapping()
    public List<AppUser> getAll() {
        return (List<AppUser>) userRepo.findAll();
    }

    @PostMapping
    public AppUser create(@RequestBody AppUser user) {
        return userRepo.save(user);
    }

    @GetMapping("/{userId}/perks")
    public List<Perk> getUserPerks(@PathVariable Long userId) {
        AppUser user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return perkRepo.findByPostedBy(user);
    }

    @PostMapping("/login")
    public AppUser login(@RequestBody AppUser loginRequest) {
        AppUser user = userRepo.findByEmail(loginRequest.getEmail());
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }

    @PostMapping("/{userId}/perks")
    public Perk createPerk(@PathVariable Long userId, @RequestBody Perk perk) {
        Optional<AppUser> user = userRepo.findById(userId);

        perk.setPostedBy(user.orElse(null));

        perk.setUpvotes(0);
        perk.setDownvotes(0);

        return perkRepo.save(perk);
    }
}