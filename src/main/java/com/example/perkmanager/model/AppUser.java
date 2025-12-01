package com.example.perkmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    //one-to-one relationship with Profile
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    // Many-to-many relationship: users can have many perks and a perk can belong to many users
    @ManyToMany
    @JoinTable(
        name = "user_perks",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "perk_id")
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Perk> perks = new ArrayList<>();

    public AppUser() {} // JPA requires this

    public AppUser(String email, String password) {
        this.email = email;
        this.password = password;
        this.profile = new Profile();
    }

    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }

    public List<Perk> getPerks() {
        return perks;
    }
    public void setPerks(List<Perk> perks) {
        this.perks = perks;
    }
    public void addPerk(Perk perk) {
        if (perk == null) return;
        if (!this.perks.contains(perk)) {
            this.perks.add(perk);
        }
    }
    public void removePerk(Perk perk) {
        this.perks.remove(perk);
    }
}
