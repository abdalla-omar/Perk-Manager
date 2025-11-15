package com.example.perkmanager.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query: Get perks matching user profile
 * Read-only operation to retrieve personalized perks based on user memberships
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPerksMatchingProfileQuery {

    @NotNull(message = "User ID is required")
    private Long userId;
}
