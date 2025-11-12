package com.example.perkmanager.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // List of memberships: Air Miles, CAA, Visa, etc.
    @ElementCollection
    private Set<String> memberships = new HashSet<>();

    public Profile() {}

    public boolean hasMembership(String membership) {
        return memberships.contains(membership);
    }

    public void addMembership(String membership) {
        memberships.add(membership);
    }

    public void removeMembership(String membership) {
        memberships.remove(membership);
    }

    public Long getId() {
        return id;
    }

    public Set<String> getMemberships() {
        return memberships;
    }
}