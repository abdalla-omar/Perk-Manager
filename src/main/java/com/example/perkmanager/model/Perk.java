package com.example.perkmanager.model;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Perk {
    @Id @GeneratedValue
    private Long id;
    private String description;
    @Enumerated(EnumType.STRING)
    private MembershipType membership;

    @Enumerated(EnumType.STRING)
    private ProductType product;
    private int upvotes;
    private int downvotes;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private AppUser postedBy;

    protected Perk() {} // JPA requires this

    public Perk(String description, MembershipType membership, ProductType product, LocalDate startDate, LocalDate endDate, AppUser postedBy) {
        this.description = description;
        this.membership = membership;
        this.product = product;
        this.startDate = startDate;
        this.endDate = endDate;
        this.postedBy = postedBy;
        this.upvotes = 0;
        this.downvotes = 0;
    }

    public void upvote() { upvotes++; }
    public void downvote() { downvotes++; }

    //getters and setters
    public Long getId() { return id; }
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

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public AppUser getPostedBy() { return postedBy; }
    public void setPostedBy(AppUser postedBy) { this.postedBy = postedBy; }

    @Override
    public String toString() {
        return "Perk{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", membership='" + membership + '\'' +
                ", product='" + product + '\'' +
                ", upvotes=" + upvotes +
                ", downvotes=" + downvotes +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", postedBy=" + (postedBy != null ? postedBy.getEmail() : "null") +
                '}';
    }
}