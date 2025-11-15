package com.example.perkmanager.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query: Get perks sorted by votes
 * Read-only operation to retrieve perks ordered by upvote count
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPerksByVotesQuery {
    private boolean descending = true; // Sort by highest votes first
}
