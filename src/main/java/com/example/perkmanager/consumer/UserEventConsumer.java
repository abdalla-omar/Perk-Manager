package com.example.perkmanager.consumer;

import com.example.perkmanager.event.MembershipAddedEvent;
import com.example.perkmanager.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka Event Consumer: User and Profile Events
 * Listens to user/profile events and updates read models
 */
@Component
public class UserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    /**
     * Consumer: UserRegisteredEvent
     * Can trigger welcome emails, analytics, etc.
     */
    @KafkaListener(
            topics = "${kafka.topic.user-registered}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeUserRegistered(@Payload UserRegisteredEvent event) {
        log.info("Consumed UserRegisteredEvent - User ID: {}, Email: {}",
                event.getUserId(), event.getEmail());

        // TODO: In production, send welcome email and initialize user profile in read model
    }

    /**
     * Consumer: MembershipAddedEvent
     * Can trigger personalized recommendations
     */
    @KafkaListener(
            topics = "${kafka.topic.membership-added}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeMembershipAdded(@Payload MembershipAddedEvent event) {
        log.info("Consumed MembershipAddedEvent - User ID: {}, Profile ID: {}, Membership: {}",
                event.getUserId(), event.getProfileId(), event.getMembership());

        // TODO: In production, update read model and trigger personalized recommendations
    }
}
