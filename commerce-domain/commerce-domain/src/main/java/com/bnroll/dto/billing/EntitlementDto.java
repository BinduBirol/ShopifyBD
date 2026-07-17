package com.bnroll.dto.billing;

import com.bnroll.commercedomain.enums.billing.EntitlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Information about a user's entitlement for a specific feature.")
public class EntitlementDto {

    @Schema(
            description = "Whether the feature has been purchased or is available to the user/workspace/facility.",
            example = "true"
    )
    private boolean purchased;

    @Schema(
            description = "Type of entitlement.",
            example = "LIFETIME",
            allowableValues = {"LIFETIME", "SUBSCRIPTION", "QUOTA"}
    )
    private EntitlementType type;

    @Schema(
            description = "Maximum quota granted. Applicable only for QUOTA entitlements.",
            example = "10",
            nullable = true
    )
    private Integer quota;

    @Schema(
            description = "Amount of quota already consumed. Applicable only for QUOTA entitlements.",
            example = "3",
            nullable = true
    )
    private Integer used;

    @Schema(
            description = "Remaining quota available. Applicable only for QUOTA entitlements.",
            example = "7",
            nullable = true
    )
    private Integer remaining;

    @Schema(
            description = "Expiration date and time for subscription entitlements in UTC. Null for lifetime or quota entitlements.",
            example = "2027-07-17T00:00:00Z",
            nullable = true
    )
    private Instant expiresAt;
}