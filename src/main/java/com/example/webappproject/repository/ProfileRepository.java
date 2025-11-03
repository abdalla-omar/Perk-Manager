package com.example.webappproject.repository;

import com.example.webappproject.model.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
    Profile findById(long id);
}
