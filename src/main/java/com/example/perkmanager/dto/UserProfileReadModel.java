package com.example.perkmanager.dto;

import java.util.Set;

/**
 * Read Model: Optimized representation of User Profile for queries
 */
public class UserProfileReadModel {
    private Long userId;
    private String email;
    private Long profileId;
    private Set<String> memberships;

    public UserProfileReadModel() {}

    public UserProfileReadModel(Long userId, String email, Long profileId, Set<String> memberships) {
        this.userId = userId;
        this.email = email;
        this.profileId = profileId;
        this.memberships = memberships;
    }

    /**
     * Factory method to create from AppUser entity
     */
    public static UserProfileReadModel fromEntity(com.example.perkmanager.model.AppUser user) {
        UserProfileReadModel model = new UserProfileReadModel();
        model.setUserId(user.getId());
        model.setEmail(user.getEmail());

        if (user.getProfile() != null) {
            model.setProfileId(user.getProfile().getId());
            model.setMemberships(user.getProfile().getMemberships());
        }

        return model;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public Set<String> getMemberships() { return memberships; }
    public void setMemberships(Set<String> memberships) { this.memberships = memberships; }
}
