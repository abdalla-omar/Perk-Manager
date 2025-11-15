package com.example.perkmanager.service;

import com.example.perkmanager.command.AddMembershipCommand;
import com.example.perkmanager.command.CreateUserCommand;
import com.example.perkmanager.event.MembershipAddedEvent;
import com.example.perkmanager.event.UserRegisteredEvent;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Profile;
import com.example.perkmanager.repository.ProfileRepository;
import com.example.perkmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Command Handler: User and Profile Write Operations
 * Handles commands that modify user/profile state and publishes events
 */
@Service
@Slf4j
public class UserCommandHandler {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EventPublisher eventPublisher;

    public UserCommandHandler(UserRepository userRepository,
                              ProfileRepository profileRepository,
                              EventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
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
        user.setPassword(command.getPassword()); // TODO: Hash password in production

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
}
