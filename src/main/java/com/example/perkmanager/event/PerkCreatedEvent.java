package com.example.perkmanager.event;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain Event: Published when a new perk is created
 * Used to update read models and search indexes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerkCreatedEvent {
    private Long perkId;
    private String description;
    private MembershipType membership;
    private ProductType product;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long postedByUserId;
    private LocalDateTime timestamp;
}
