package com.bnroll.property.controller;

import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.property.dto.FacilityRequest;
import com.bnroll.property.entity.Facility;
import com.bnroll.property.security.UserPrincipal;
import com.bnroll.property.service.FacilityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/facility")
public class FacilityController {
    private final FacilityService facilityService;
    private final MessageSource messageSource;

    @PostMapping("/create")
    public ApiResponse<String> create(@Valid @RequestBody FacilityRequest request, HttpServletRequest httpServletRequest, Locale locale, Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        Facility facility = facilityService.create(request, user);

        String responseMessage = messageSource.getMessage("facility.created", null, locale);
        return ApiResponse.<String>builder()
                .success(true)
                .data(responseMessage)
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(httpServletRequest.getRequestURI())
                .correlationId(String.valueOf(facility.getId()))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<FacilityRequest>> getFacilities(
            HttpServletRequest httpServletRequest,
            Locale locale,
            Authentication authentication
    ) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        List<FacilityRequest> list = facilityService.findAllByUserId(user.id());

        return ApiResponse.<List<FacilityRequest>>builder()
                .success(true)
                .data(list)
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(httpServletRequest.getRequestURI())
                .correlationId(String.valueOf(user.id()))
                .build();
    }
}
