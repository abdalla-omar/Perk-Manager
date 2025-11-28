package com.example.perkmanager.repository;

import com.example.perkmanager.model.AppUser;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.PerkVote;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PerkVoteRepository extends CrudRepository<PerkVote, Long> {
    Optional<PerkVote> findByUserAndPerk(AppUser user, Perk perk);
}
