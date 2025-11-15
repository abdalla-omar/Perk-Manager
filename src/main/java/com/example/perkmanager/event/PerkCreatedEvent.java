package com.example.perkmanager.event;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain Event: Published when a new perk is created
 * Used to update read models and search indexes
 */
public class PerkCreatedEvent {
    private Long perkId;
    private String description;
    private MembershipType membership;
    private ProductType product;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long postedByUserId;
    private LocalDateTime timestamp;

    public PerkCreatedEvent() {}

    public PerkCreatedEvent(Long perkId, String description, MembershipType membership,
                           ProductType product, LocalDate startDate, LocalDate endDate,
                           Long postedByUserId, LocalDateTime timestamp) {
        this.perkId = perkId;
        this.description = description;
        this.membership = membership;
        this.product = product;
        this.startDate = startDate;
        this.endDate = endDate;
        this.postedByUserId = postedByUserId;
        this.timestamp = timestamp;
    }

    public Long getPerkId() { return perkId; }
    public void setPerkId(Long perkId) { this.perkId = perkId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public MembershipType getMembership() { return membership; }
    public void setMembership(MembershipType membership) { this.membership = membership; }
    public ProductType getProduct() { return product; }
    public void setProduct(ProductType product) { this.product = product; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Long getPostedByUserId() { return postedByUserId; }
    public void setPostedByUserId(Long postedByUserId) { this.postedByUserId = postedByUserId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
