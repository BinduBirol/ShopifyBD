package com.bnroll.billing.controller;

import com.bnroll.commercedomain.enums.billing.EntitlementType;
import com.bnroll.commercedomain.enums.billing.Feature;
import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.dto.billing.EntitlementDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class BillingController {

    @GetMapping("/check/entitlements")
    public ApiResponse<EntitlementDto> check(
            @RequestParam Feature feature,
            @RequestParam(required = false) UUID workspaceId,
            Authentication authentication,
            HttpServletRequest request) {

        EntitlementDto entitlement = EntitlementDto.builder()
                .purchased(true)
                .type(EntitlementType.LIFETIME)
                .quota(1)
                .used(1)
                .remaining(0)
                .expiresAt(null)
                .build();

        return ApiResponse.<EntitlementDto>builder()
                .success(true)
                .data(entitlement)
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(request.getRequestURI())
                .correlationId(String.valueOf(-1))
                .build();
    }
}
