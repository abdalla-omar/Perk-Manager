package com.example.perkmanager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Configuration for CQRS Event Streaming
 * Creates all necessary topics for domain events
 */
@Configuration
public class KafkaConfig {

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

    /**
     * Topic for Perk Creation events
     * Partitions: 3 for parallel processing
     * Replicas: 1 (increase in production)
     */
    @Bean
    public NewTopic perkCreatedTopic() {
        return TopicBuilder.name(perkCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic perkUpvotedTopic() {
        return TopicBuilder.name(perkUpvotedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic perkDownvotedTopic() {
        return TopicBuilder.name(perkDownvotedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic)
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic membershipAddedTopic() {
        return TopicBuilder.name(membershipAddedTopic)
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic membershipRemovedTopic() {
        return TopicBuilder.name(membershipRemovedTopic)
                .partitions(2)
                .replicas(1)
                .build();
    }
}
