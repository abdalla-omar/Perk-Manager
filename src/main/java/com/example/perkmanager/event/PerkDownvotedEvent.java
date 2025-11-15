package com.example.perkmanager.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain Event: Published when a perk receives a downvote
 * Used to update vote counts in read models
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerkDownvotedEvent {
    private Long perkId;
    private int newDownvoteCount;
    private LocalDateTime timestamp;
}
