package com.example.perkmanager.event;

import java.time.LocalDateTime;

/**
 * Domain Event: Published when a perk receives a downvote
 * Used to update vote counts in read models
 */
public class PerkDownvotedEvent {
    private Long perkId;
    private int newDownvoteCount;
    private LocalDateTime timestamp;

    public PerkDownvotedEvent() {}

    public PerkDownvotedEvent(Long perkId, int newDownvoteCount, LocalDateTime timestamp) {
        this.perkId = perkId;
        this.newDownvoteCount = newDownvoteCount;
        this.timestamp = timestamp;
    }

    public Long getPerkId() { return perkId; }
    public void setPerkId(Long perkId) { this.perkId = perkId; }
    public int getNewDownvoteCount() { return newDownvoteCount; }
    public void setNewDownvoteCount(int newDownvoteCount) { this.newDownvoteCount = newDownvoteCount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
