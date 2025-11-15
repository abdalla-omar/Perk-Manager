package com.example.perkmanager.repository;

import com.example.perkmanager.model.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findById(Long id); // Change return type to Optional<AppUser>
    AppUser findByEmail(String email);
}