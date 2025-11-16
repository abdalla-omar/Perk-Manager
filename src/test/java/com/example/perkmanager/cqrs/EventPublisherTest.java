package com.example.perkmanager.cqrs;

import com.example.perkmanager.event.PerkCreatedEvent;
import com.example.perkmanager.event.PerkUpvotedEvent;
import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import com.example.perkmanager.service.EventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit Test: EventPublisher
 * Tests event publishing to Kafka
 */
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {"perk.created", "perk.upvoted"}
)
public class EventPublisherTest {

    @Autowired
    private EventPublisher eventPublisher;

    @Test
    public void testPublishPerkCreatedEvent() {
        // Given: A PerkCreatedEvent
        PerkCreatedEvent event = new PerkCreatedEvent(
                1L,
                "Test perk",
                MembershipType.CAA,
                ProductType.CARS,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                100L,
                LocalDateTime.now()
        );

        // When/Then: Publishing should not throw exception
        assertDoesNotThrow(() -> eventPublisher.publishPerkCreated(event));
    }

    @Test
    public void testPublishPerkUpvotedEvent() {
        // Given: A PerkUpvotedEvent
        PerkUpvotedEvent event = new PerkUpvotedEvent(
                1L,
                5,
                LocalDateTime.now()
        );

        // When/Then: Publishing should not throw exception
        assertDoesNotThrow(() -> eventPublisher.publishPerkUpvoted(event));
    }
}
