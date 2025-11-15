package com.example.perkmanager.event;

import java.time.LocalDateTime;

/**
 * Domain Event: Published when a user removes a membership from their profile
 */
public class MembershipRemovedEvent {
    private Long userId;
    private Long profileId;
    private String membership;
    private LocalDateTime timestamp;

    public MembershipRemovedEvent() {}

    public MembershipRemovedEvent(Long userId, Long profileId, String membership, LocalDateTime timestamp) {
        this.userId = userId;
        this.profileId = profileId;
        this.membership = membership;
        this.timestamp = timestamp;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public String getMembership() { return membership; }
    public void setMembership(String membership) { this.membership = membership; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
