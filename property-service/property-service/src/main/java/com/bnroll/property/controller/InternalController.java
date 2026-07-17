package com.bnroll.property.controller;

import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.dto.property.FacilityDto;
import com.bnroll.property.entity.Facility;
import com.bnroll.property.security.ServicePrincipal;
import com.bnroll.property.service.FacilityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/internal"))
public class InternalController {

    private final FacilityService facilityService;
    private final MessageSource messageSource;

    @PostMapping("/facility/create")
    public ApiResponse<String> createInternal(
            @Valid @RequestBody FacilityDto request,
            HttpServletRequest httpServletRequest,
            Locale locale,
            Authentication authentication
    ) {

        if (!(authentication.getPrincipal() instanceof ServicePrincipal)) {
            throw new BadCredentialsException("Invalid service request");
        }


        Facility facility = facilityService.createForRegistration(request);


        String responseMessage =
                messageSource.getMessage(
                        "facility.created",
                        null,
                        locale
                );


        return ApiResponse.<String>builder()
                .success(true)
                .data(responseMessage)
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(httpServletRequest.getRequestURI())
                .correlationId(String.valueOf(facility.getId()))
                .build();
    }
}
