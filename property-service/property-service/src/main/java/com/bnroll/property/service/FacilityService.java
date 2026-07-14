package com.bnroll.property.service;

import com.bnroll.commercedomain.event.property.FacilityCreatedEvent;
import com.bnroll.dto.property.FacilityDto;
import com.bnroll.property.entity.Facility;
import com.bnroll.property.entity.FacilityMember;
import com.bnroll.property.event.config.KafkaProducer;
import com.bnroll.property.repository.FacilityMemberRepository;
import com.bnroll.property.repository.FacilityRepository;
import com.bnroll.property.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMemberRepository facilityMemberRepository;
    private final KafkaProducer kafkaProducer;

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
}
