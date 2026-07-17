package com.bnroll.property.service;

import com.bnroll.commercedomain.enums.billing.Feature;
import com.bnroll.commercedomain.event.property.FacilityCreatedEvent;
import com.bnroll.commercedomain.exception.BillingException;
import com.bnroll.commercedomain.exception.PropertyException;
import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.common.dto.user.UserPrincipal;
import com.bnroll.dto.billing.EntitlementDto;
import com.bnroll.dto.property.FacilityDto;
import com.bnroll.property.client.BillingClient;
import com.bnroll.property.entity.Facility;
import com.bnroll.property.entity.FacilityMember;
import com.bnroll.property.event.config.KafkaProducer;
import com.bnroll.property.repository.FacilityMemberRepository;
import com.bnroll.property.repository.FacilityRepository;
import com.bnroll.property.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMemberRepository facilityMemberRepository;
    private final KafkaProducer kafkaProducer;
    private final BillingClient billingClient;
    private final JwtService jwtService;

    public List<FacilityDto> findAllByUserId(Long userId) {
        List<FacilityMember> facilityMemberList = facilityMemberRepository.findByUserId(userId);

        return facilityMemberList
                .stream()
                .map(fm -> {
                    Facility facility = fm.getFacility();

                    return FacilityDto.builder()
                            .id(facility.getId())
                            .name(facility.getName())
                            .type(facility.getType())
                            .addressLine1(facility.getAddressLine1())
                            .addressLine2(facility.getAddressLine2())
                            .city(facility.getCity())
                            .country(facility.getCountry())
                            .postalCode(facility.getPostalCode())
                            .description(facility.getDescription())
                            .userRole(fm.getRole())
                            .build();
                })
                .toList();
    }

    @Transactional
    public Facility create(@Valid FacilityDto request, UserPrincipal user) {

        checkBillingStatus(Feature.FACILITY, null, user);

        Facility facility = Facility.builder()
                .name(request.getName())
                .type(request.getType())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .description(request.getDescription())
                .build();


        Facility saveFacility = facilityRepository.save(facility);

        FacilityMember member = FacilityMember.builder()
                .facility(saveFacility)
                .userId(user.id())
                .role(request.getUserRole())
                .build();
        facilityMemberRepository.save(member);


        FacilityCreatedEvent event = new FacilityCreatedEvent(
                UUID.randomUUID(),
                user.id(),
                facility.getId(),
                facility.getName(),
                facility.getType().name(),
                LocalDateTime.now()
        );

        kafkaProducer.facilityCreationEvent(event);

        return saveFacility;
    }


    @Transactional
    public Facility createForRegistration(@Valid FacilityDto request) {


        Facility facility = Facility.builder()
                .name(request.getName())
                .type(request.getType())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .description(request.getDescription())
                .build();


        Facility saveFacility = facilityRepository.save(facility);

        FacilityMember member = FacilityMember.builder()
                .facility(saveFacility)
                .userId(request.getCreatorId())
                .role(request.getUserRole())
                .build();
        facilityMemberRepository.save(member);


        FacilityCreatedEvent event = new FacilityCreatedEvent(
                UUID.randomUUID(),
                request.getCreatorId(),
                facility.getId(),
                facility.getName(),
                facility.getType().name(),
                LocalDateTime.now()
        );

        kafkaProducer.facilityCreationEvent(event);

        return saveFacility;
    }

    public void checkBillingStatus(Feature feature, UUID workspaceId, UserPrincipal user) {

        EntitlementDto entitlement;

        try {
            entitlement = billingClient.checkFeature(feature, workspaceId, user.id());
        } catch (Exception e) {
            throw new PropertyException(
                    "error.billing.service.unavailable",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }

        if (entitlement == null || !entitlement.isPurchased()) {
            throw new PropertyException(
                    "error.billing.feature.not.purchased",
                    HttpStatus.PAYMENT_REQUIRED,
                    feature.name(),
                    feature.name()
            );
        }

        if (entitlement.getQuota() != null
                && entitlement.getUsed() != null
                && entitlement.getUsed() >= entitlement.getQuota()) {

            throw new PropertyException(
                    "error.billing.quota.exceeded",
                    HttpStatus.PAYMENT_REQUIRED,
                    feature.name(),
                    feature.name()
            );
        }

        if (entitlement.getExpiresAt() != null
                && entitlement.getExpiresAt().isBefore(Instant.now())) {

            throw new PropertyException(
                    "error.billing.subscription.expired",
                    HttpStatus.PAYMENT_REQUIRED,
                    feature.name(),
                    feature.name()
            );
        }
    }
}
