package com.example.perkmanager.controller;

import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.Profile;
import com.example.perkmanager.repository.PerkRepository;
import com.example.perkmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/perkmanager")
public class AppController {

    private final UserRepository userRepo;
    private final PerkRepository perkRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppController(UserRepository userRepo,
                         PerkRepository perkRepo,
                         PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.perkRepo = perkRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------------------------------------------------------------
    // Users
    // ---------------------------------------------------------------------

    // POST /api/perkmanager  (create user)
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody AppUser user) {
        // Basic validation
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Password is required");
        }

        // Check if email is already in use
        AppUser existing = userRepo.findByEmail(user.getEmail());
        if (existing != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already in use");
        }

        // Hash password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Ensure profile exists
        if (user.getProfile() == null) {
            user.setProfile(new Profile());
        }

        AppUser saved = userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // POST /api/perkmanager/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AppUser loginRequest) {
        AppUser user = userRepo.findByEmail(loginRequest.getEmail());
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }

        // Verify password using BCrypt
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }

        return ResponseEntity.ok(user);
    }

    // get all users
    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok((List<AppUser>) userRepo.findAll());
    }

    // ---------------------------------------------------------------------
    // Perks
    // ---------------------------------------------------------------------

    @GetMapping("/perks")
    public List<Perk> getAllPerks() {
        return (List<Perk>) perkRepo.findAll();
    }

    // GET /api/perkmanager/{userId}/perks  -> perks for a specific user
    @GetMapping("/{userId}/perks")
    public ResponseEntity<List<Perk>> getUserPerks(@PathVariable Long userId) {
        Optional<AppUser> maybeUser = userRepo.findById(userId);
        if (maybeUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser user = maybeUser.get();
        List<Perk> perks = perkRepo.findByPostedBy(user);
        return ResponseEntity.ok(perks);
    }

    // POST /api/perkmanager/{userId}/perks  -> create perk for a user
    @PostMapping("/{userId}/perks")
    public ResponseEntity<Perk> createPerk(@PathVariable Long userId,
                                           @RequestBody Perk perk) {
        Optional<AppUser> maybeUser = userRepo.findById(userId);
        if (maybeUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AppUser user = maybeUser.get();
        perk.setPostedBy(user);

        // ensure default votes
        if (perk.getUpvotes() < 0) perk.setUpvotes(0);
        if (perk.getDownvotes() < 0) perk.setDownvotes(0);

        Perk saved = perkRepo.save(perk);
        return ResponseEntity.ok(saved);
    }

    // POST /api/perkmanager/perks/{perkId}/upvote
    @PostMapping("/perks/{perkId}/upvote")
    public ResponseEntity<Perk> upvotePerk(@PathVariable Long perkId) {
        return perkRepo.findById(perkId)
                .map(perk -> {
                    perk.upvote();
                    perkRepo.save(perk);
                    return ResponseEntity.ok(perk);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/perkmanager/{userId}/password
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId,
                                           @RequestBody Map<String, String> body) {
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        // Validation
        if (currentPassword == null || currentPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Current password is required");
        }
        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("New password is required");
        }
        if (currentPassword.equals(newPassword)) {
            return ResponseEntity.badRequest().body("New password must be different from current password");
        }

        // Find user
        Optional<AppUser> maybeUser = userRepo.findById(userId);
        if (maybeUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AppUser user = maybeUser.get();

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Current password is incorrect");
        }

        // Hash and update new password
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    // ---------------------------------------------------------------------
    // Profile & Memberships
    // ---------------------------------------------------------------------

    // GET /api/perkmanager/{userId}/profile
    @GetMapping("/{userId}/profile")
    public ResponseEntity<Set<String>> getProfile(@PathVariable Long userId) {
        Optional<AppUser> maybeUser = userRepo.findById(userId);
        if (maybeUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AppUser user = maybeUser.get();
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            user.setProfile(profile);
            userRepo.save(user);
        }

        return ResponseEntity.ok(profile.getMemberships());
    }

    // POST /api/perkmanager/{userId}/profile
    // Expected body: { "membership": "VISA" }
    @PostMapping("/{userId}/profile")
    public ResponseEntity<Set<String>> addMembership(@PathVariable Long userId,
                                                     @RequestBody Map<String, String> body) {
        String membership = body.get("membership");
        if (membership == null || membership.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<AppUser> maybeUser = userRepo.findById(userId);
        if (maybeUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AppUser user = maybeUser.get();
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            user.setProfile(profile);
        }

        profile.addMembership(membership);
        // Save user (cascade may save profile as well)
        userRepo.save(user);

        return ResponseEntity.ok(profile.getMemberships());
    }
}
