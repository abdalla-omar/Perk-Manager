package com.example.perkmanager.service;

import com.example.perkmanager.dto.PerkReadModel;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.query.*;
import com.example.perkmanager.repository.PerkRepository;
import com.example.perkmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Query Handler: Perk Read Operations
 * Handles queries that read perk data without side effects
 * Optimized for read performance
 */
@Service
public class PerkQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(PerkQueryHandler.class);

    private final PerkRepository perkRepository;
    private final UserRepository userRepository;

    public PerkQueryHandler(PerkRepository perkRepository, UserRepository userRepository) {
        this.perkRepository = perkRepository;
        this.userRepository = userRepository;
    }

    /**
     * Handle GetAllPerksQuery
     * Returns all perks as read models
     */
    public List<PerkReadModel> handle(GetAllPerksQuery query) {
        log.info("Handling GetAllPerksQuery");

        return StreamSupport.stream(perkRepository.findAll().spliterator(), false)
                .map(PerkReadModel::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Handle GetPerksByVotesQuery
     * Returns perks sorted by upvote count
     */
    public List<PerkReadModel> handle(GetPerksByVotesQuery query) {
        log.info("Handling GetPerksByVotesQuery (descending: {})", query.isDescending());

        List<Perk> perks = perkRepository.findAllByOrderByUpvotesDesc();

        return perks.stream()
                .map(PerkReadModel::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Handle GetPerksByMembershipQuery
     * Returns perks filtered by membership type
     */
    public List<PerkReadModel> handle(GetPerksByMembershipQuery query) {
        log.info("Handling GetPerksByMembershipQuery for membership: {}", query.getMembership());

        List<Perk> perks = perkRepository.findByMembership(query.getMembership());

        return perks.stream()
                .map(PerkReadModel::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Handle GetPerksMatchingProfileQuery
     * Returns perks that match user's memberships (personalized)
     */
    public Map<String, Set<PerkReadModel>> handle(GetPerksMatchingProfileQuery query) {
        log.info("Handling GetPerksMatchingProfileQuery for user: {}", query.getUserId());

        // Load user and profile
        AppUser user = userRepository.findById(query.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + query.getUserId()));

        if (user.getProfile() == null || user.getProfile().getMemberships().isEmpty()) {
            log.warn("User {} has no memberships", query.getUserId());
            return Map.of(); // Return empty map if no memberships
        }

        // Get all perks
        Set<Perk> allPerks = StreamSupport.stream(perkRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());

        // Filter perks for "Your Perks" list (perks posted by the user)
        Set<PerkReadModel> userPerks = perkRepository.findByPostedBy(user)
                .stream()
                .map(PerkReadModel::fromEntity)
                .collect(Collectors.toSet());

        // Categorize perks by membership for "All Perks"
        Map<String, Set<PerkReadModel>> categorizedPerks = new HashMap<>();
        allPerks.forEach(perk -> {
            String membership = perk.getMembership().name();
            categorizedPerks
                    .computeIfAbsent(membership, k -> new HashSet<>())
                    .add(PerkReadModel.fromEntity(perk));
        });

        // Add "Your Perks" list to the map
        categorizedPerks.put("Your Perks", userPerks);

        return categorizedPerks;
    }
}
