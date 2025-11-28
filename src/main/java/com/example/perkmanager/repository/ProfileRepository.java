package com.example.perkmanager.repository;

import com.example.perkmanager.model.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}
