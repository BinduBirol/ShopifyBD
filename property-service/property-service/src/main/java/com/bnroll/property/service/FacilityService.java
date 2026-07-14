package com.bnroll.property.service;

import com.bnroll.property.dto.FacilityRequest;
import com.bnroll.property.entity.Facility;
import com.bnroll.property.entity.FacilityMember;
import com.bnroll.property.repository.FacilityMemberRepository;
import com.bnroll.property.repository.FacilityRepository;
import com.bnroll.property.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMemberRepository facilityMemberRepository;

    public List<FacilityRequest> findAllByUserId(Long userId) {
        List<FacilityMember> facilityMemberList = facilityMemberRepository.findByUserId(userId);

        return facilityMemberList
                .stream()
                .map(fm -> {
                    Facility facility = fm.getFacility();

                    return FacilityRequest.builder()
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
    public Facility create(@Valid FacilityRequest request, UserPrincipal user) {


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

        return saveFacility;
    }
}
