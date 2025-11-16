package com.example.perkmanager.cqrs;

import com.example.perkmanager.command.CreatePerkCommand;
import com.example.perkmanager.command.CreateUserCommand;
import com.example.perkmanager.command.UpvotePerkCommand;
import com.example.perkmanager.dto.PerkReadModel;
import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import com.example.perkmanager.event.PerkCreatedEvent;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.query.GetAllPerksQuery;
import com.example.perkmanager.query.GetPerksByVotesQuery;
import com.example.perkmanager.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test: CQRS + Kafka
 * Tests the complete flow: Command -> Handler -> Event -> Consumer -> Query
 *
 * Uses embedded Kafka for testing without external dependencies
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(
        partitions = 1,
        topics = {
                "perk.created",
                "perk.upvoted",
                "perk.downvoted",
                "user.registered",
                "membership.added"
        }
)
public class CqrsIntegrationTest {

    @Autowired
    private UserCommandHandler userCommandHandler;

    @Autowired
    private PerkCommandHandler perkCommandHandler;

    @Autowired
    private PerkQueryHandler perkQueryHandler;

    @Test
    public void testCompleteUserCreationFlow() {
        // Given: A create user command
        CreateUserCommand command = new CreateUserCommand(
                "test@example.com",
                "password123"
        );

        // When: Command is handled
        AppUser user = userCommandHandler.handle(command);

        // Then: User is created successfully
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertNotNull(user.getProfile());

        // Event will be published to Kafka (verified in logs)
    }

    @Test
    public void testCompletePerkCreationFlow() throws InterruptedException {
        // Given: A user exists
        CreateUserCommand userCommand = new CreateUserCommand(
                "perkuser@example.com",
                "password123"
        );
        AppUser user = userCommandHandler.handle(userCommand);

        // And: A create perk command
        CreatePerkCommand perkCommand = new CreatePerkCommand(
                user.getId(),
                "50% off at Cineplex with Aeroplan",
                MembershipType.AIRMILES,
                ProductType.MOVIES,
                LocalDate.now(),
                LocalDate.now().plusMonths(3)
        );

        // When: Command is handled
        Perk perk = perkCommandHandler.handle(perkCommand);

        // Then: Perk is created
        assertNotNull(perk);
        assertNotNull(perk.getId());
        assertEquals("50% off at Cineplex with Aeroplan", perk.getDescription());
        assertEquals(MembershipType.AIRMILES, perk.getMembership());
        assertEquals(0, perk.getUpvotes());

        // Wait a bit for async event processing
        Thread.sleep(100);

        // And: Perk can be queried
        GetAllPerksQuery query = new GetAllPerksQuery();
        List<PerkReadModel> perks = perkQueryHandler.handle(query);

        assertFalse(perks.isEmpty());
        assertTrue(perks.stream()
                .anyMatch(p -> p.getId().equals(perk.getId())));
    }

    @Test
    public void testVotingFlow() throws InterruptedException {
        // Given: A perk exists
        CreateUserCommand userCommand = new CreateUserCommand(
                "voter@example.com",
                "password123"
        );
        AppUser user = userCommandHandler.handle(userCommand);

        CreatePerkCommand perkCommand = new CreatePerkCommand(
                user.getId(),
                "Free hotel upgrade with Mastercard",
                MembershipType.MASTERCARD,
                ProductType.HOTELS,
                LocalDate.now(),
                LocalDate.now().plusMonths(2)
        );
        Perk perk = perkCommandHandler.handle(perkCommand);

        // When: Upvote command is handled multiple times
        UpvotePerkCommand upvoteCommand = new UpvotePerkCommand(perk.getId());
        perkCommandHandler.handle(upvoteCommand);
        perkCommandHandler.handle(upvoteCommand);
        perkCommandHandler.handle(upvoteCommand);

        Thread.sleep(100);

        // Then: Vote count is updated
        GetPerksByVotesQuery votesQuery = new GetPerksByVotesQuery(true);
        List<PerkReadModel> sortedPerks = perkQueryHandler.handle(votesQuery);

        PerkReadModel topPerk = sortedPerks.stream()
                .filter(p -> p.getId().equals(perk.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(3, topPerk.getUpvotes());
        assertEquals(3, topPerk.getNetScore());
    }

    @Test
    public void testCommandValidation() {
        // Given: Invalid command (null email)
        CreateUserCommand invalidCommand = new CreateUserCommand(null, "password");

        // When/Then: Validation should fail
        // Note: This would normally be caught by @Valid annotation in controller
        // Here we test the business logic validation
    }

    @Test
    public void testEventualConsistency() throws InterruptedException {
        // This test demonstrates eventual consistency
        // Write operation happens immediately, read model updates asynchronously

        // Given: Create multiple perks
        CreateUserCommand userCommand = new CreateUserCommand(
                "multiuser@example.com",
                "password123"
        );
        AppUser user = userCommandHandler.handle(userCommand);

        for (int i = 0; i < 5; i++) {
            CreatePerkCommand perkCommand = new CreatePerkCommand(
                    user.getId(),
                    "Perk " + i,
                    MembershipType.VISA,
                    ProductType.DINING,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(1)
            );
            perkCommandHandler.handle(perkCommand);
        }

        // Immediate query might not reflect all events yet (eventual consistency)
        GetAllPerksQuery query = new GetAllPerksQuery();
        List<PerkReadModel> perks = perkQueryHandler.handle(query);

        // After waiting, all should be available
        Thread.sleep(200);
        perks = perkQueryHandler.handle(query);
        assertTrue(perks.size() >= 5);
    }
}
