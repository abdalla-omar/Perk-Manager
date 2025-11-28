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
    @CollectionTable(name = "profile_membership", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "membership")
    private Set<String> memberships = new HashSet<>();

    public Profile() {}

    public void addMembership(String membership) {
        memberships.add(membership);
    }

    public Long getId() {
        return id;
    }

    public Set<String> getMemberships() {
        return memberships;
    }
}