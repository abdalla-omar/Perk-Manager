package com.example.perkmanager.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command: Downvote a perk
 * Represents the intention to increase the downvote count of a perk
 */
public class DownvotePerkCommand {

    @NotNull(message = "Perk ID is required")
    private Long perkId;

    @NotNull(message = "User ID is required")
    private Long userId;


    public DownvotePerkCommand() {}

    public DownvotePerkCommand(Long perkId, Long userId) {
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
