package com.bnroll.property.client;

import com.bnroll.commercedomain.enums.ServiceName;
import com.bnroll.commercedomain.enums.billing.Feature;
import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.dto.billing.EntitlementDto;
import com.bnroll.dto.property.FacilityDto;
import com.bnroll.property.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BillingClient {

    private final RestClient billingRestClient;
    private final JwtService jwtService;

    public EntitlementDto checkFeature(Feature feature, UUID workspaceId, Long userId) {

        String token = jwtService.generateServiceToken(ServiceName.PROPERTY_SERVICE.value());

        ApiResponse<EntitlementDto> response = billingRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/core/check/entitlements")
                        .queryParam("feature", feature)
                        .queryParamIfPresent("workspaceId", Optional.ofNullable(workspaceId))
                        .queryParamIfPresent("userId", Optional.ofNullable(userId))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<EntitlementDto>>() {});

        System.out.println(response.getData());

        return response.getData();
    }


}