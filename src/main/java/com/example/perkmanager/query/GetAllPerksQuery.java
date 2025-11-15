package com.example.perkmanager.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query: Get all perks
 * Read-only operation to retrieve all perks from read model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPerksQuery {
    // No parameters needed for getting all perks
    // Can be extended with pagination later
    private Integer page;
    private Integer size;
}
