package com.example.perkmanager.controller;

import com.example.perkmanager.command.*;
import com.example.perkmanager.dto.PerkReadModel;
import com.example.perkmanager.dto.UserProfileReadModel;
import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.query.*;
import com.example.perkmanager.service.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CQRS Controller
 * Demonstrates Command Query Responsibility Segregation pattern
 *
 * Key Principles:
 * - Commands (POST/PUT/DELETE) -> CommandHandlers -> Write DB -> Publish Events
 * - Queries (GET) -> QueryHandlers -> Read Models
 * - Clear separation between reads and writes
 * - Events published to Kafka for eventual consistency
 *
 * Base Path: /api/cqrs
 */
@RestController
@RequestMapping("/api/cqrs")
public class CqrsController {

    private static final Logger log = LoggerFactory.getLogger(CqrsController.class);

    // Command Handlers (Write Operations)
    private final PerkCommandHandler perkCommandHandler;
    private final UserCommandHandler userCommandHandler;

    // Query Handlers (Read Operations)
    private final PerkQueryHandler perkQueryHandler;
    private final UserQueryHandler userQueryHandler;

    public CqrsController(PerkCommandHandler perkCommandHandler,
                          UserCommandHandler userCommandHandler,
                          PerkQueryHandler perkQueryHandler,
                          UserQueryHandler userQueryHandler) {
        this.perkCommandHandler = perkCommandHandler;
        this.userCommandHandler = userCommandHandler;
        this.perkQueryHandler = perkQueryHandler;
        this.userQueryHandler = userQueryHandler;
    }

    // =====================================================================
    // COMMANDS - Write Operations (Modify State + Publish Events)
    // =====================================================================

    /**
     * Command: Create User
     * POST /api/cqrs/users
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserCommand command) {
        try {
            log.info("Received CreateUserCommand: {}", command.getEmail());
            var user = userCommandHandler.handle(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserProfileReadModel.fromEntity(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Command: Create Perk
     * POST /api/cqrs/perks
     */
    @PostMapping("/perks")
    public ResponseEntity<?> createPerk(@Valid @RequestBody CreatePerkCommand command) {
        try {
            log.info("Received CreatePerkCommand for user: {}", command.getUserId());
            Perk perk = perkCommandHandler.handle(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(PerkReadModel.fromEntity(perk));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Command: Upvote Perk
     * POST /api/cqrs/perks/{perkId}/upvote
     */
    @PostMapping("/perks/{perkId}/upvote")
    public ResponseEntity<?> upvotePerk(@PathVariable Long perkId) {
        try {
            log.info("Received UpvotePerkCommand for perk: {}", perkId);
            UpvotePerkCommand command = new UpvotePerkCommand(perkId);
            Perk perk = perkCommandHandler.handle(command);
            return ResponseEntity.ok(PerkReadModel.fromEntity(perk));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Command: Downvote Perk
     * POST /api/cqrs/perks/{perkId}/downvote
     */
    @PostMapping("/perks/{perkId}/downvote")
    public ResponseEntity<?> downvotePerk(@PathVariable Long perkId) {
        try {
            log.info("Received DownvotePerkCommand for perk: {}", perkId);
            DownvotePerkCommand command = new DownvotePerkCommand(perkId);
            Perk perk = perkCommandHandler.handle(command);
            return ResponseEntity.ok(PerkReadModel.fromEntity(perk));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Command: Add Membership to Profile
     * POST /api/cqrs/users/{userId}/memberships
     */
    @PostMapping("/users/{userId}/memberships")
    public ResponseEntity<?> addMembership(
            @PathVariable Long userId,
            @RequestBody AddMembershipCommand command) {
        try {
            log.info("Received AddMembershipCommand for user: {}", userId);
            command.setUserId(userId); // Set from path variable
            userCommandHandler.handle(command);
            return ResponseEntity.ok("Membership added successfully");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =====================================================================
    // QUERIES - Read Operations (No Side Effects)
    // =====================================================================

    /**
     * Query: Get All Perks
     * GET /api/cqrs/perks
     */
    @GetMapping("/perks")
    public ResponseEntity<List<PerkReadModel>> getAllPerks() {
        log.info("Received GetAllPerksQuery");
        GetAllPerksQuery query = new GetAllPerksQuery();
        List<PerkReadModel> perks = perkQueryHandler.handle(query);
        return ResponseEntity.ok(perks);
    }

    /**
     * Query: Get Perks by Votes (Sorted)
     * GET /api/cqrs/perks/by-votes
     */
    @GetMapping("/perks/by-votes")
    public ResponseEntity<List<PerkReadModel>> getPerksByVotes() {
        log.info("Received GetPerksByVotesQuery");
        GetPerksByVotesQuery query = new GetPerksByVotesQuery(true);
        List<PerkReadModel> perks = perkQueryHandler.handle(query);
        return ResponseEntity.ok(perks);
    }

    /**
     * Query: Get Perks by Membership Type
     * GET /api/cqrs/perks/by-membership/{membership}
     */
    @GetMapping("/perks/by-membership/{membership}")
    public ResponseEntity<?> getPerksByMembership(@PathVariable String membership) {
        try {
            log.info("Received GetPerksByMembershipQuery for: {}", membership);
            MembershipType membershipType = MembershipType.valueOf(membership.toUpperCase());
            GetPerksByMembershipQuery query = new GetPerksByMembershipQuery(membershipType);
            List<PerkReadModel> perks = perkQueryHandler.handle(query);
            return ResponseEntity.ok(perks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid membership type: " + membership);
        }
    }

    /**
     * Query: Get Perks Matching User Profile (Personalized)
     * GET /api/cqrs/users/{userId}/matching-perks
     */
    @GetMapping("/users/{userId}/matching-perks")
    public ResponseEntity<?> getPerksMatchingProfile(@PathVariable Long userId) {
        try {
            log.info("Received GetPerksMatchingProfileQuery for user: {}", userId);
            GetPerksMatchingProfileQuery query = new GetPerksMatchingProfileQuery(userId);
            Map<String, List<PerkReadModel>> categorizedPerks = perkQueryHandler.handle(query);

            // Flatten the map into a single list
            List<PerkReadModel> perks = categorizedPerks.values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(perks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Query: Get User Profile
     * GET /api/cqrs/users/{userId}/profile
     */
    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            log.info("Received GetUserProfileQuery for user: {}", userId);
            GetUserProfileQuery query = new GetUserProfileQuery(userId);
            UserProfileReadModel profile = userQueryHandler.handle(query);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =====================================================================
    // Health Check
    // =====================================================================

    /**
     * Health check endpoint to verify CQRS system is operational
     * GET /api/cqrs/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CQRS System is operational");
    }
}
