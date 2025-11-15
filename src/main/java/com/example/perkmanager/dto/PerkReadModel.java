package com.example.perkmanager.dto;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;

import java.time.LocalDate;

/**
 * Read Model: Optimized representation of Perk for queries
 * This is separate from the write model (Perk entity) to allow
 * independent optimization of read and write operations
 */
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

    public PerkReadModel() {}

    public PerkReadModel(Long id, String description, MembershipType membership, ProductType product,
                         int upvotes, int downvotes, int netScore, LocalDate startDate, LocalDate endDate,
                         String postedByEmail, Long postedByUserId, boolean isActive) {
        this.id = id;
        this.description = description;
        this.membership = membership;
        this.product = product;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.netScore = netScore;
        this.startDate = startDate;
        this.endDate = endDate;
        this.postedByEmail = postedByEmail;
        this.postedByUserId = postedByUserId;
        this.isActive = isActive;
    }

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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public MembershipType getMembership() { return membership; }
    public void setMembership(MembershipType membership) { this.membership = membership; }
    public ProductType getProduct() { return product; }
    public void setProduct(ProductType product) { this.product = product; }
    public int getUpvotes() { return upvotes; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
    public int getDownvotes() { return downvotes; }
    public void setDownvotes(int downvotes) { this.downvotes = downvotes; }
    public int getNetScore() { return netScore; }
    public void setNetScore(int netScore) { this.netScore = netScore; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getPostedByEmail() { return postedByEmail; }
    public void setPostedByEmail(String postedByEmail) { this.postedByEmail = postedByEmail; }
    public Long getPostedByUserId() { return postedByUserId; }
    public void setPostedByUserId(Long postedByUserId) { this.postedByUserId = postedByUserId; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
