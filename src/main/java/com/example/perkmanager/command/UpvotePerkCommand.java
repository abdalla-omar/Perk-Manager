package com.example.perkmanager.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command: Upvote a perk
 * Represents the intention to increase the upvote count of a perk
 */
public class UpvotePerkCommand {

    @NotNull(message = "Perk ID is required")
    private Long perkId;

    public UpvotePerkCommand() {}

    public UpvotePerkCommand(Long perkId) {
        this.perkId = perkId;
    }

    public Long getPerkId() {
        return perkId;
    }

    public void setPerkId(Long perkId) {
        this.perkId = perkId;
    }
}
