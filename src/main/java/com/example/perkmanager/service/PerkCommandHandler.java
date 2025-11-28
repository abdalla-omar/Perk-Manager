package com.example.perkmanager.service;

import com.example.perkmanager.command.CreatePerkCommand;
import com.example.perkmanager.command.DownvotePerkCommand;
import com.example.perkmanager.command.UpvotePerkCommand;
import com.example.perkmanager.enumerations.VoteType;
import com.example.perkmanager.event.PerkCreatedEvent;
import com.example.perkmanager.event.PerkDownvotedEvent;
import com.example.perkmanager.event.PerkUpvotedEvent;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.PerkVote;
import com.example.perkmanager.repository.PerkRepository;
import com.example.perkmanager.repository.PerkVoteRepository;
import com.example.perkmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Command Handler: Perk Write Operations
 * Handles commands that modify perk state and publishes events
 */
@Service
public class PerkCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(PerkCommandHandler.class);

    private final PerkRepository perkRepository;
    private final UserRepository userRepository;
    private final PerkVoteRepository perkVoteRepository;
    private final EventPublisher eventPublisher;

    public PerkCommandHandler(PerkRepository perkRepository,
                              UserRepository userRepository,
                              PerkVoteRepository perkVoteRepository,
                              EventPublisher eventPublisher) {
        this.perkRepository = perkRepository;
        this.userRepository = userRepository;
        this.perkVoteRepository = perkVoteRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Handle CreatePerkCommand
     * Creates a new perk and publishes PerkCreatedEvent
     */
    @Transactional
    public Perk handle(CreatePerkCommand command) {
        log.info("Handling CreatePerkCommand for user {}", command.getUserId());

        // Validate user exists
        AppUser user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.getUserId()));

        // Create perk entity using constructor
        Perk perk = new Perk(
                command.getDescription(),
                command.getMembership(),
                command.getProduct(),
                command.getStartDate(),
                command.getEndDate(),
                user
        );
        user.addPerk(perk);
        System.out.println("User perks after adding new perk: " + user.getPerks());
        System.out.println("Size of user perk list: " + user.getPerks().size());
        // Save to write database
        Perk savedPerk = perkRepository.save(perk);
        log.info("Created perk with ID: {}", savedPerk.getId());

        // Publish domain event
        PerkCreatedEvent event = new PerkCreatedEvent(
                savedPerk.getId(),
                savedPerk.getDescription(),
                savedPerk.getMembership(),
                savedPerk.getProduct(),
                savedPerk.getStartDate(),
                savedPerk.getEndDate(),
                user.getId(),
                LocalDateTime.now()
        );
        eventPublisher.publishPerkCreated(event);

        return savedPerk;
    }

    /**
     * Handle UpvotePerkCommand
     * Increments upvote count and publishes PerkUpvotedEvent
     */
    @Transactional
    public Perk handle(UpvotePerkCommand command) {
        log.info("Handling UpvotePerkCommand for perk {}, user {}",
                command.getPerkId(), command.getUserId());

        Perk perk = perkRepository.findById(command.getPerkId())
                .orElseThrow(() -> new IllegalArgumentException("Perk not found: " + command.getPerkId()));

        AppUser user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.getUserId()));

        var existingVoteOpt = perkVoteRepository.findByUserAndPerk(user, perk);

        if (existingVoteOpt.isEmpty()) {
            // No existing vote -> add new upvote
            perk.setUpvotes(perk.getUpvotes() + 1);
            PerkVote vote = new PerkVote(user, perk, VoteType.UPVOTE);
            perkVoteRepository.save(vote);
        } else {
            PerkVote existing = existingVoteOpt.get();
            switch (existing.getVoteType()) {
                case UPVOTE -> {
                    // Toggle off (remove upvote)
                    perk.setUpvotes(perk.getUpvotes() - 1);
                    perkVoteRepository.delete(existing);
                }
                case DOWNVOTE -> {
                    // Switch from downvote to upvote
                    perk.setDownvotes(perk.getDownvotes() - 1);
                    perk.setUpvotes(perk.getUpvotes() + 1);
                    existing.setVoteType(VoteType.UPVOTE);
                    perkVoteRepository.save(existing);
                }
            }
        }

        Perk updatedPerk = perkRepository.save(perk);

        // Publish event with updated count
        PerkUpvotedEvent event = new PerkUpvotedEvent(
                updatedPerk.getId(),
                updatedPerk.getUpvotes(),
                LocalDateTime.now()
        );
        eventPublisher.publishPerkUpvoted(event);

        return updatedPerk;
    }


    /**
     * Handle DownvotePerkCommand
     * Increments downvote count and publishes PerkDownvotedEvent
     */
    @Transactional
    public Perk handle(DownvotePerkCommand command) {
        log.info("Handling DownvotePerkCommand for perk {}, user {}",
                command.getPerkId(), command.getUserId());

        Perk perk = perkRepository.findById(command.getPerkId())
                .orElseThrow(() -> new IllegalArgumentException("Perk not found: " + command.getPerkId()));

        AppUser user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.getUserId()));

        var existingVoteOpt = perkVoteRepository.findByUserAndPerk(user, perk);

        if (existingVoteOpt.isEmpty()) {
            // No existing vote -> add new downvote
            perk.setDownvotes(perk.getDownvotes() + 1);
            PerkVote vote = new PerkVote(user, perk, VoteType.DOWNVOTE);
            perkVoteRepository.save(vote);
        } else {
            PerkVote existing = existingVoteOpt.get();
            switch (existing.getVoteType()) {
                case DOWNVOTE -> {
                    // Toggle off (remove downvote)
                    perk.setDownvotes(perk.getDownvotes() - 1);
                    perkVoteRepository.delete(existing);
                }
                case UPVOTE -> {
                    // Switch from upvote to downvote
                    perk.setUpvotes(perk.getUpvotes() - 1);
                    perk.setDownvotes(perk.getDownvotes() + 1);
                    existing.setVoteType(VoteType.DOWNVOTE);
                    perkVoteRepository.save(existing);
                }
            }
        }

        Perk updatedPerk = perkRepository.save(perk);

        PerkDownvotedEvent event = new PerkDownvotedEvent(
                updatedPerk.getId(),
                updatedPerk.getDownvotes(),
                LocalDateTime.now()
        );
        eventPublisher.publishPerkDownvoted(event);

        return updatedPerk;
    }

}
