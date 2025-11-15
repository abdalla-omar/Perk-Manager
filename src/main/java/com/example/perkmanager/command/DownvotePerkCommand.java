package com.example.perkmanager.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command: Downvote a perk
 * Represents the intention to increase the downvote count of a perk
 */
public class DownvotePerkCommand {

    @NotNull(message = "Perk ID is required")
    private Long perkId;

    public DownvotePerkCommand() {}

    public DownvotePerkCommand(Long perkId) {
        this.perkId = perkId;
    }

    public Long getPerkId() {
        return perkId;
    }

    public void setPerkId(Long perkId) {
        this.perkId = perkId;
    }
}
