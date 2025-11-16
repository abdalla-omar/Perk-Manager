package com.example.perkmanager.query;

import com.example.perkmanager.enumerations.MembershipType;
import jakarta.validation.constraints.NotNull;

/**
 * Query: Get perks by membership type
 * Read-only operation to filter perks by membership
 */
public class GetPerksByMembershipQuery {

    @NotNull(message = "Membership type is required")
    private MembershipType membership;

    public GetPerksByMembershipQuery() {}

    public GetPerksByMembershipQuery(MembershipType membership) {
        this.membership = membership;
    }

    public MembershipType getMembership() {
        return membership;
    }

    public void setMembership(MembershipType membership) {
        this.membership = membership;
    }
}
