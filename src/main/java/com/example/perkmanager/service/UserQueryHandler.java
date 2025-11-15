package com.example.perkmanager.service;

import com.example.perkmanager.dto.UserProfileReadModel;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.query.GetUserProfileQuery;
import com.example.perkmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Query Handler: User Profile Read Operations
 * Handles queries that read user/profile data without side effects
 */
@Service
@Slf4j
public class UserQueryHandler {

    private final UserRepository userRepository;

    public UserQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Handle GetUserProfileQuery
     * Returns user profile with memberships
     */
    public UserProfileReadModel handle(GetUserProfileQuery query) {
        log.info("Handling GetUserProfileQuery for user: {}", query.getUserId());

        AppUser user = userRepository.findById(query.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + query.getUserId()));

        return UserProfileReadModel.fromEntity(user);
    }
}
