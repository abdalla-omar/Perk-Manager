package com.example.perkmanager.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command: Add an existing Perk to a User
 */
public class AddPerkCommand {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Perk ID is required")
    private Long perkId;

    public AddPerkCommand() {}

    public AddPerkCommand(Long userId, Long perkId) {
        this.userId = userId;
        this.perkId = perkId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPerkId() {
        return perkId;
    }

    public void setPerkId(Long perkId) {
        this.perkId = perkId;
    }
}