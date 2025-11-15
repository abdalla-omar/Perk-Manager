package com.example.perkmanager.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command: Upvote a perk
 * Represents the intention to increase the upvote count of a perk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpvotePerkCommand {

    @NotNull(message = "Perk ID is required")
    private Long perkId;
}
