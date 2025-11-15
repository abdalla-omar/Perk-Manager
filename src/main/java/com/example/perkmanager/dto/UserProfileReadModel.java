package com.example.perkmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Read Model: Optimized representation of User Profile for queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileReadModel {
    private Long userId;
    private String email;
    private Long profileId;
    private Set<String> memberships;

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
}
