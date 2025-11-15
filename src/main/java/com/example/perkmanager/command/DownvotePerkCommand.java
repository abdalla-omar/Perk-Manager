package com.example.perkmanager.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command: Downvote a perk
 * Represents the intention to increase the downvote count of a perk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownvotePerkCommand {

    @NotNull(message = "Perk ID is required")
    private Long perkId;
}
