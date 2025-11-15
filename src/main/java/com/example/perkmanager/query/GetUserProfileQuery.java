package com.example.perkmanager.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query: Get user profile
 * Read-only operation to retrieve user profile and memberships
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserProfileQuery {

    @NotNull(message = "User ID is required")
    private Long userId;
}
