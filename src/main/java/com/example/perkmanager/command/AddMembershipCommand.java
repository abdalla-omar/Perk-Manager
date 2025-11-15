package com.example.perkmanager.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Command: Add a membership to user profile
 * Represents the intention to add a new membership (e.g., CAA, VISA)
 */
public class AddMembershipCommand {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Membership is required")
    private String membership;

    public AddMembershipCommand() {}

    public AddMembershipCommand(Long userId, String membership) {
        this.userId = userId;
        this.membership = membership;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }
}
