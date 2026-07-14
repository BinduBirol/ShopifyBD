package com.bnroll.commercedomain.event.property;


import java.time.LocalDateTime;
import java.util.UUID;

public record FacilityCreatedEvent(
        UUID eventId,
        Long userId,
        UUID facilityId,
        String facilityName,
        String facilityType,
        LocalDateTime createdAt
) {
}