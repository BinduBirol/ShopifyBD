package com.bnroll.property.dto;

import com.bnroll.commercedomain.enums.user.RoleName;
import com.bnroll.property.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating or updating a facility")
public class FacilityRequest {

    @NotBlank(message = "facility.name.required")
    @Size(max = 150, message = "facility.name.maxLength")
    @Schema(
            description = "Name of the facility",
            example = "Gulshan Tower",
            maxLength = 150,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;


    @NotNull(message = "facility.type.required")
    @Schema(
            description = "Type of the facility",
            example = "COMMERCIAL",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private FacilityType type;


    @NotBlank(message = "facility.addressLine1.required")
    @Size(max = 255, message = "facility.addressLine1.maxLength")
    @Schema(
            description = "Primary address of the facility",
            example = "House 25, Road 12",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String addressLine1;


    @Size(max = 255, message = "facility.addressLine2.maxLength")
    @Schema(
            description = "Additional address information",
            example = "Gulshan Avenue",
            maxLength = 255
    )
    private String addressLine2;


    @Size(max = 100, message = "facility.city.maxLength")
    @Schema(
            description = "City where the facility is located",
            example = "Dhaka",
            maxLength = 100
    )
    private String city;


    @Size(max = 100, message = "facility.country.maxLength")
    @Schema(
            description = "Country where the facility is located",
            example = "Bangladesh",
            maxLength = 100
    )
    private String country;


    @Size(max = 20, message = "facility.postalCode.maxLength")
    @Schema(
            description = "Postal code of the facility",
            example = "1212",
            maxLength = 20
    )
    private String postalCode;


    @Size(max = 500, message = "facility.description.maxLength")
    @Schema(
            description = "Additional description of the facility",
            example = "Commercial building with office units",
            maxLength = 500
    )
    private String description;

    @NotNull(message = "facility.role.required")
    @Schema(
            description = "Role of the user creating the facility",
            example = "OWNER"
    )
    private RoleName userRole;
}