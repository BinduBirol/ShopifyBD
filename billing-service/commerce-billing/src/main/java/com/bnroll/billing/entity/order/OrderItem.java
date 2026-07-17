package com.bnroll.billing.entity.order;

import com.bnroll.commercedomain.entity.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private UUID productId;

    /**
     * Snapshot of the product name at purchase time.
     */
    @Column(nullable = false)
    private String productName;

    /**
     * Price per unit at purchase time.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    /**
     * unitPrice * quantity
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;
}