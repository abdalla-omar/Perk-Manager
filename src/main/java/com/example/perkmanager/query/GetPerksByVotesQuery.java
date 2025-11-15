package com.example.perkmanager.query;

/**
 * Query: Get perks sorted by votes
 * Read-only operation to retrieve perks ordered by upvote count
 */
public class GetPerksByVotesQuery {
    private boolean descending = true; // Sort by highest votes first

    public GetPerksByVotesQuery() {}

    public GetPerksByVotesQuery(boolean descending) {
        this.descending = descending;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }
}
