package com.example.perkmanager.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command: Add a membership to user profile
 * Represents the intention to add a new membership (e.g., CAA, VISA)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMembershipCommand {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Membership is required")
    private String membership;
}
