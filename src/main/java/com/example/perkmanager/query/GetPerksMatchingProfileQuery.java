package com.example.perkmanager.query;

import jakarta.validation.constraints.NotNull;

/**
 * Query: Get perks matching user profile
 * Read-only operation to retrieve personalized perks based on user memberships
 */
public class GetPerksMatchingProfileQuery {

    @NotNull(message = "User ID is required")
    private Long userId;

    public GetPerksMatchingProfileQuery() {}

    public GetPerksMatchingProfileQuery(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
