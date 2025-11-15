package com.example.perkmanager.event;

import java.time.LocalDateTime;

/**
 * Domain Event: Published when a perk receives an upvote
 * Used to update vote counts in read models
 */
public class PerkUpvotedEvent {
    private Long perkId;
    private int newUpvoteCount;
    private LocalDateTime timestamp;

    public PerkUpvotedEvent() {}

    public PerkUpvotedEvent(Long perkId, int newUpvoteCount, LocalDateTime timestamp) {
        this.perkId = perkId;
        this.newUpvoteCount = newUpvoteCount;
        this.timestamp = timestamp;
    }

    public Long getPerkId() { return perkId; }
    public void setPerkId(Long perkId) { this.perkId = perkId; }
    public int getNewUpvoteCount() { return newUpvoteCount; }
    public void setNewUpvoteCount(int newUpvoteCount) { this.newUpvoteCount = newUpvoteCount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
