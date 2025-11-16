package com.example.perkmanager.service;

import com.example.perkmanager.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Event Publisher Service
 * Responsible for publishing domain events to Kafka topics
 * Uses async publishing with callback handling
 */
@Service
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.perk-created}")
    private String perkCreatedTopic;

    @Value("${kafka.topic.perk-upvoted}")
    private String perkUpvotedTopic;

    @Value("${kafka.topic.perk-downvoted}")
    private String perkDownvotedTopic;

    @Value("${kafka.topic.user-registered}")
    private String userRegisteredTopic;

    @Value("${kafka.topic.membership-added}")
    private String membershipAddedTopic;

    @Value("${kafka.topic.membership-removed}")
    private String membershipRemovedTopic;

    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publish PerkCreatedEvent
     * Key: perkId for partitioning
     */
    public void publishPerkCreated(PerkCreatedEvent event) {
        String key = String.valueOf(event.getPerkId());
        publish(perkCreatedTopic, key, event, "PerkCreated");
    }

    /**
     * Publish PerkUpvotedEvent
     */
    public void publishPerkUpvoted(PerkUpvotedEvent event) {
        String key = String.valueOf(event.getPerkId());
        publish(perkUpvotedTopic, key, event, "PerkUpvoted");
    }

    /**
     * Publish PerkDownvotedEvent
     */
    public void publishPerkDownvoted(PerkDownvotedEvent event) {
        String key = String.valueOf(event.getPerkId());
        publish(perkDownvotedTopic, key, event, "PerkDownvoted");
    }

    /**
     * Publish UserRegisteredEvent
     */
    public void publishUserRegistered(UserRegisteredEvent event) {
        String key = String.valueOf(event.getUserId());
        publish(userRegisteredTopic, key, event, "UserRegistered");
    }

    /**
     * Publish MembershipAddedEvent
     */
    public void publishMembershipAdded(MembershipAddedEvent event) {
        String key = String.valueOf(event.getUserId());
        publish(membershipAddedTopic, key, event, "MembershipAdded");
    }

    /**
     * Publish MembershipRemovedEvent
     */
    public void publishMembershipRemoved(MembershipRemovedEvent event) {
        String key = String.valueOf(event.getUserId());
        publish(membershipRemovedTopic, key, event, "MembershipRemoved");
    }

    /**
     * Generic publish method with async callback handling
     * Gracefully handles Kafka unavailability - app continues to work even if Kafka is down
     */
    private void publish(String topic, String key, Object event, String eventType) {
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✓ Published {} event to topic [{}] with key [{}] at offset [{}]",
                            eventType, topic, key, result.getRecordMetadata().offset());
                } else {
                    log.warn("⚠ Failed to publish {} event (Kafka unavailable): {}",
                            eventType, ex.getMessage());
                    // App continues to work - events just won't be published
                }
            });
        } catch (Exception e) {
            // Kafka is down - log warning but don't fail the request
            log.warn("⚠ Kafka unavailable - {} event not published. App continues to work normally.",
                    eventType);
            // In production: implement retry logic, dead letter queue, or store events locally
        }
    }
}
