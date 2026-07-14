package com.bnroll.property.entity;

import com.bnroll.commercedomain.entity.BaseEntity;
import com.bnroll.commercedomain.enums.user.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "facility_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;


    @Column(nullable = false)
    private Long userId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName role;
}