package com.example.perkmanager.service;

import com.example.perkmanager.dto.UserProfileReadModel;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.query.GetUserProfileQuery;
import com.example.perkmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Query Handler: User Profile Read Operations
 * Handles queries that read user/profile data without side effects
 */
@Service
public class UserQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(UserQueryHandler.class);

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
