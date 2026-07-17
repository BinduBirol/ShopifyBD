package com.bnroll.billing.entity.core;

import com.bnroll.billing.entity.order.Order;
import com.bnroll.commercedomain.entity.BaseEntity;
import com.bnroll.commercedomain.enums.billing.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "entitlements")
public class Entitlement extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    private UUID workspaceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Feature feature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EntitlementType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EntitlementStatus status;

    /**
     * Only used for QUOTA entitlements.
     * Example: quota = 10 means the user may create 10 properties.
     */
    private Integer quota;

    /**
     * Number already consumed.
     */
    private Integer used;

    /**
     * Only used for SUBSCRIPTION entitlements.
     */
    private Instant expiresAt;

    /**
     * Which purchase granted this entitlement.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private EntitlementScope scope;
}