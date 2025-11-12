package com.example.perkmanager.model;

import jakarta.persistence.*;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    //one-to-one relationship with Profile
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

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
}
