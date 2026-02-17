package com.elias.orderservice.infrastructure.order.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_orders")
@Data
class OrderEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID investorId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;
}
