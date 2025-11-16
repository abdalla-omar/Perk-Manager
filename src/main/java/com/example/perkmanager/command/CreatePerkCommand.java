package com.example.perkmanager.command;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Command: Create a new perk
 * Represents the intention to post a new perk offer
 */
public class CreatePerkCommand {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Membership type is required")
    private MembershipType membership;

    @NotNull(message = "Product type is required")
    private ProductType product;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    public CreatePerkCommand() {}

    public CreatePerkCommand(Long userId, String description, MembershipType membership,
                            ProductType product, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.description = description;
        this.membership = membership;
        this.product = product;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
}
