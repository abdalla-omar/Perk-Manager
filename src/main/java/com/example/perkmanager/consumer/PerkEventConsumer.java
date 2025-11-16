package com.example.perkmanager.consumer;

import com.example.perkmanager.event.PerkCreatedEvent;
import com.example.perkmanager.event.PerkDownvotedEvent;
import com.example.perkmanager.event.PerkUpvotedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka Event Consumer: Perk Events
 * Listens to perk-related events and updates read models
 *
 * In a full CQRS implementation, this would:
 * 1. Update separate read model database/cache
 * 2. Update search indexes (Elasticsearch, etc.)
 * 3. Trigger analytics/notifications
 * 4. Update materialized views
 */
@Component
public class PerkEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PerkEventConsumer.class);

    /**
     * Consumer: PerkCreatedEvent
     * Updates read model when new perk is created
     */
    @KafkaListener(
            topics = "${kafka.topic.perk-created}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePerkCreated(
            @Payload PerkCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Consumed PerkCreatedEvent - Key: {}, Partition: {}, Offset: {}", key, partition, offset);
        log.info("Perk ID: {}, Description: {}, Membership: {}, Product: {}",
                event.getPerkId(), event.getDescription(), event.getMembership(), event.getProduct());

        // TODO: In production, update read model database/cache
        // Example:
        // 1. Insert into read-optimized table (e.g., perk_read_model)
        // 2. Update Elasticsearch index for search
        // 3. Update Redis cache for fast access
        // 4. Trigger notifications to interested users

        updateSearchIndex(event);
    }

    /**
     * Consumer: PerkUpvotedEvent
     * Updates vote counts in read model
     */
    @KafkaListener(
            topics = "${kafka.topic.perk-upvoted}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumePerkUpvoted(@Payload PerkUpvotedEvent event) {
        log.info("Consumed PerkUpvotedEvent - Perk ID: {}, New Upvote Count: {}",
                event.getPerkId(), event.getNewUpvoteCount());

        // TODO: Update read model with new vote count
        // This is typically faster than querying the write database
        updateVoteCountInReadModel(event.getPerkId(), event.getNewUpvoteCount(), null);
    }

    /**
     * Consumer: PerkDownvotedEvent
     * Updates vote counts in read model
     */
    @KafkaListener(
            topics = "${kafka.topic.perk-downvoted}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumePerkDownvoted(@Payload PerkDownvotedEvent event) {
        log.info("Consumed PerkDownvotedEvent - Perk ID: {}, New Downvote Count: {}",
                event.getPerkId(), event.getNewDownvoteCount());

        // TODO: Update read model with new vote count
        updateVoteCountInReadModel(event.getPerkId(), null, event.getNewDownvoteCount());
    }

    /**
     * Helper: Update search index (placeholder for Elasticsearch, etc.)
     */
    private void updateSearchIndex(PerkCreatedEvent event) {
        log.debug("Updating search index for perk {}", event.getPerkId());
        // In production:
        // - elasticsearchClient.index(event)
        // - Allows fast text search on perk descriptions
        // - Geo-search for location-based perks
    }

    /**
     * Helper: Update vote counts in read model (placeholder)
     */
    private void updateVoteCountInReadModel(Long perkId, Integer upvotes, Integer downvotes) {
        log.debug("Updating vote counts in read model for perk {}", perkId);
        // In production:
        // - Update denormalized read table
        // - Update Redis cache
        // - Recalculate rankings/scores
    }
}
