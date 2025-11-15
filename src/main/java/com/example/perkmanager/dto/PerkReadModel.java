package com.example.perkmanager.dto;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Read Model: Optimized representation of Perk for queries
 * This is separate from the write model (Perk entity) to allow
 * independent optimization of read and write operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerkReadModel {
    private Long id;
    private String description;
    private MembershipType membership;
    private ProductType product;
    private int upvotes;
    private int downvotes;
    private int netScore; // Calculated: upvotes - downvotes
    private LocalDate startDate;
    private LocalDate endDate;
    private String postedByEmail;
    private Long postedByUserId;
    private boolean isActive; // Calculated based on dates

    /**
     * Factory method to create from Perk entity
     */
    public static PerkReadModel fromEntity(com.example.perkmanager.model.Perk perk) {
        PerkReadModel model = new PerkReadModel();
        model.setId(perk.getId());
        model.setDescription(perk.getDescription());
        model.setMembership(perk.getMembership());
        model.setProduct(perk.getProduct());
        model.setUpvotes(perk.getUpvotes());
        model.setDownvotes(perk.getDownvotes());
        model.setNetScore(perk.getUpvotes() - perk.getDownvotes());
        model.setStartDate(perk.getStartDate());
        model.setEndDate(perk.getEndDate());
        model.setPostedByUserId(perk.getPostedBy() != null ? perk.getPostedBy().getId() : null);
        model.setPostedByEmail(perk.getPostedBy() != null ? perk.getPostedBy().getEmail() : null);

        // Check if perk is currently active
        LocalDate now = LocalDate.now();
        model.setActive(now.isAfter(perk.getStartDate()) && now.isBefore(perk.getEndDate()));

        return model;
    }
}
