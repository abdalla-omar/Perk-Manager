package com.example.perkmanager.service;

import com.example.perkmanager.command.AddMembershipCommand;
import com.example.perkmanager.command.AddPerkCommand;
import com.example.perkmanager.command.CreateUserCommand;
import com.example.perkmanager.event.MembershipAddedEvent;
import com.example.perkmanager.event.PerkAddedEvent;
import com.example.perkmanager.event.UserRegisteredEvent;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.Profile;
import com.example.perkmanager.repository.PerkRepository;
import com.example.perkmanager.repository.ProfileRepository;
import com.example.perkmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Command Handler: User and Profile Write Operations
 * Handles commands that modify user/profile state and publishes events
 */
@Service
public class UserCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(UserCommandHandler.class);

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PerkRepository perkRepository;
    private final EventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public UserCommandHandler(UserRepository userRepository,
                              ProfileRepository profileRepository,
                              EventPublisher eventPublisher,
                              PasswordEncoder passwordEncoder, PerkRepository perkRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
        this.perkRepository = perkRepository;
    }

    /**
     * Handle CreateUserCommand
     * Creates a new user with empty profile and publishes UserRegisteredEvent
     */
    @Transactional
    public AppUser handle(CreateUserCommand command) {
        log.info("Handling CreateUserCommand for email: {}", command.getEmail());

        // Check if user already exists
        AppUser existingUser = userRepository.findByEmail(command.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("User already exists with email: " + command.getEmail());
        }

        // Create user with profile
        AppUser user = new AppUser();
        user.setEmail(command.getEmail());
        // Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(command.getPassword());
        user.setPassword(hashedPassword);

        Profile profile = new Profile();
        user.setProfile(profile);

        // Save to write database
        AppUser savedUser = userRepository.save(user);
        log.info("Created user with ID: {}", savedUser.getId());

        // Publish domain event
        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                LocalDateTime.now()
        );
        eventPublisher.publishUserRegistered(event);

        return savedUser;
    }

    /**
     * Handle AddMembershipCommand
     * Adds membership to user profile and publishes MembershipAddedEvent
     */
    @Transactional
    public Profile handle(AddMembershipCommand command) {
        log.info("Handling AddMembershipCommand for user {}: {}",
                command.getUserId(), command.getMembership());

        // Load user
        AppUser user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.getUserId()));

        Profile profile = user.getProfile();
        if (profile == null) {
            throw new IllegalStateException("User has no profile: " + command.getUserId());
        }

        // Add membership
        boolean added = profile.getMemberships().add(command.getMembership());
        if (!added) {
            log.warn("Membership {} already exists for user {}",
                    command.getMembership(), command.getUserId());
            return profile; // Already exists, no event needed
        }

        // Save to write database
        Profile savedProfile = profileRepository.save(profile);
        log.info("Added membership {} to profile {}", command.getMembership(), profile.getId());

        // Publish event
        MembershipAddedEvent event = new MembershipAddedEvent(
                user.getId(),
                profile.getId(),
                command.getMembership(),
                LocalDateTime.now()
        );
        eventPublisher.publishMembershipAdded(event);

        return savedProfile;
    }

    /**
     * Handle AddPerkCommand
     * Adds a perk to the user's list of perks and publishes PerkAddedEvent
     */
    @Transactional
    public AppUser handle(AddPerkCommand command) {
        log.info("Handling AddPerkCommand for user {}: perk {}",
                command.getUserId(), command.getPerkId());

        // Load user
        AppUser user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.getUserId()));


        // Add perk (store perkId, not full object)
        Perk perk = perkRepository.findById(command.getPerkId())
                .orElseThrow(() -> new IllegalArgumentException("Perk not found: " + command.getPerkId()));

        user.getPerks().add(perk);
        System.out.println("User perks after addition: " + user.getPerks());
        System.out.println("\nSize of user perks: " + user.getPerks().size());

        // Save updated user
        AppUser savedUser = userRepository.save(user);
        log.info("Added perk {} to user {}", command.getPerkId(), command.getUserId());

        // Publish event
        PerkAddedEvent event = new PerkAddedEvent(
                savedUser.getId(),
                perk
        );
        eventPublisher.publishPerkAdded(event);

        return savedUser;
    }
}
