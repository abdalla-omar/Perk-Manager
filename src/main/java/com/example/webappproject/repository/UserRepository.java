package com.example.webappproject.repository;

import com.example.webappproject.model.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<AppUser, Long> {
    AppUser findById(long id);
    AppUser findByEmail(String email);
}
