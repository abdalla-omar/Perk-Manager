package com.example.webappproject.repository;

import com.example.webappproject.enumerations.MembershipType;
import com.example.webappproject.model.AppUser;
import com.example.webappproject.model.Perk;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PerkRepository extends CrudRepository<Perk, Long> {
    List<Perk> findAllByOrderByUpvotesDesc();
    List<Perk> findByMembership(MembershipType membership);
    List<Perk> findByPostedBy(AppUser user);
    Perk findById(long id);
}
