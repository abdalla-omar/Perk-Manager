package com.example.perkmanager.repository;

import com.example.perkmanager.enumerations.MembershipType;
import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PerkRepository extends CrudRepository<Perk, Long> {
    List<Perk> findAllByOrderByUpvotesDesc();
    List<Perk> findByMembership(MembershipType membership);
    List<Perk> findByPostedBy(AppUser user);
    Perk findById(long id);
}
