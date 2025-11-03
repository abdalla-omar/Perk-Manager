package com.example.perkmanager.model;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.enumerations.ProductType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PerkTest {

    @Test
    void testPerkCreation() {
        AppUser user = new AppUser("test@example.com", "password");
        Perk perk = new Perk(
                "Test Description",
                MembershipType.VISA,
                ProductType.HOTELS,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                user
        );

        assertEquals("Test Description", perk.getDescription());
        assertEquals(MembershipType.VISA, perk.getMembership());
        assertEquals(ProductType.HOTELS, perk.getProduct());
        assertEquals(LocalDate.of(2023, 1, 1), perk.getStartDate());
        assertEquals(LocalDate.of(2023, 12, 31), perk.getEndDate());
        assertEquals(user, perk.getPostedBy());
        assertEquals(0, perk.getUpvotes());
        assertEquals(0, perk.getDownvotes());
    }

    @Test
    void testUpvoteAndDownvote() {
        Perk perk = new Perk(
                "Test Description",
                MembershipType.CAA,
                ProductType.MOVIES,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                null
        );

        perk.upvote();
        perk.upvote();
        perk.downvote();

        assertEquals(2, perk.getUpvotes());
        assertEquals(1, perk.getDownvotes());
    }

    @Test
    void testSetters() {
        Perk perk = new Perk(
                "Initial Description",
                MembershipType.AMEX,
                ProductType.CARS,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                null
        );

        perk.setDescription("Updated Description");
        perk.setMembership(MembershipType.AIRMILES);
        perk.setProduct(ProductType.DINING);

        assertEquals("Updated Description", perk.getDescription());
        assertEquals(MembershipType.AIRMILES, perk.getMembership());
        assertEquals(ProductType.DINING, perk.getProduct());
    }
}