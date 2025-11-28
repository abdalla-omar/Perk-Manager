package com.example.perkmanager.model;

import com.example.perkmanager.enumerations.VoteType;
import jakarta.persistence.*;

@Entity
@Table(
        name = "perk_vote",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "perk_id"})
        }
)
public class PerkVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "perk_id")
    private Perk perk;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    protected PerkVote() {}

    public PerkVote(AppUser user, Perk perk, VoteType voteType) {
        this.user = user;
        this.perk = perk;
        this.voteType = voteType;
    }

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public Perk getPerk() { return perk; }
    public VoteType getVoteType() { return voteType; }
    public void setVoteType(VoteType voteType) { this.voteType = voteType; }
}
