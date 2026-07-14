package com.bnroll.property.repository;

import com.bnroll.property.entity.Facility;
import com.bnroll.property.entity.FacilityMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FacilityMemberRepository extends CrudRepository<FacilityMember, UUID> {

    @Query("""
                select fm
                from FacilityMember fm
                join fetch fm.facility
                where fm.userId = :userId
            """)
    List<FacilityMember> findByUserId(@Param("userId") Long userId);
}