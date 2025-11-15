package com.example.perkmanager.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain Event: Published when a user adds a membership to their profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipAddedEvent {
    private Long userId;
    private Long profileId;
    private String membership;
    private LocalDateTime timestamp;
}
