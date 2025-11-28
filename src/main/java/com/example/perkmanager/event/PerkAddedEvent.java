package com.example.perkmanager.event;

import com.example.perkmanager.model.Perk;

public class PerkAddedEvent {

    private final Long userId;     // The user who received the perk
    private final Perk perk;       // The actual perk object

    public PerkAddedEvent(Long userId, Perk perk) {
        this.userId = userId;
        this.perk = perk;
    }

    public Long getUserId() {
        return userId;
    }

    public Perk getPerk() {
        return perk;
    }

    @Override
    public String toString() {
        return "PerkAddedEvent{" +
                "userId=" + userId +
                ", perkId=" + (perk != null ? perk.getId() : null) +
                ", description='" + (perk != null ? perk.getDescription() : null) + '\'' +
                ", membership=" + (perk != null ? perk.getMembership() : null) +
                ", product=" + (perk != null ? perk.getProduct() : null) +
                '}';
    }
}
