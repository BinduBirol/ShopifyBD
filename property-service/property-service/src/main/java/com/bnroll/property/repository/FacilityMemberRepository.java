package com.bnroll.property.repository;

import com.bnroll.property.entity.Facility;
import com.bnroll.property.entity.FacilityMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface FacilityMemberRepository extends CrudRepository<FacilityMember, UUID> {

    @Query("""
                select fm
                from FacilityMember fm
                where fm.userId = :userId
            """)
    List<FacilityMember> findByUserId(Long userId);
}
