package com.example.perkmanager.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command: Upvote a perk
 * Represents the intention to increase the upvote count of a perk
 */
public class UpvotePerkCommand {

    @NotNull(message = "Perk ID is required")
    private Long perkId;

    @NotNull(message = "User ID is required")
    private Long userId;

    public UpvotePerkCommand() {}



    public UpvotePerkCommand(Long perkId, Long userId) {

        this.perkId = perkId;
        this.userId = userId;
    }

    public Long getPerkId() {
        return perkId;
    }

    public void setPerkId(Long perkId) {
        this.perkId = perkId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
