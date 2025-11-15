package com.example.perkmanager.command;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Command: Create a new perk
 * Represents the intention to post a new perk offer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Future(message = "End date must be in the future")
    private LocalDate endDate;
}
