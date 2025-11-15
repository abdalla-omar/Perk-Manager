package com.example.perkmanager.query;

import com.example.perkmanager.enumerations.MembershipType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query: Get perks by membership type
 * Read-only operation to filter perks by membership
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPerksByMembershipQuery {

    @NotNull(message = "Membership type is required")
    private MembershipType membership;
}
