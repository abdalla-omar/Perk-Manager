package com.example.perkmanager.query;

import jakarta.validation.constraints.NotNull;

/**
 * Query: Get user profile
 * Read-only operation to retrieve user profile and memberships
 */
public class GetUserProfileQuery {

    @NotNull(message = "User ID is required")
    private Long userId;

    public GetUserProfileQuery() {}

    public GetUserProfileQuery(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
