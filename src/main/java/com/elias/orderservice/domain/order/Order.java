package com.elias.orderservice.domain.order;

import com.elias.investcommon.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Order {

    private UUID id;
    private UUID investorId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private Order(UUID investorId, UUID productId, Integer quantity, BigDecimal unitPrice) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.status = OrderStatus.CREATED;
        this.investorId = investorId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = calculateTotal(quantity, unitPrice);
    }

    public static Order create(UUID investorId, UUID productId, Integer quantity, BigDecimal unitPrice) {
        return new Order(investorId, productId, quantity, unitPrice);
    }

    private BigDecimal calculateTotal(Integer qtd, BigDecimal price) {
        if (qtd == null || price == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(qtd));
    }

    public void markAsValidated() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Order can only be validated if currently CREATED");
        }
        this.status = OrderStatus.VALIDATED;
        this.updatedAt = Instant.now();
    }

    public void approveRisk() {
        if (this.status != OrderStatus.VALIDATED) {
            throw new IllegalStateException("Risk check requires VALIDATED status");
        }
        this.status = OrderStatus.RISK_APPROVED;
        this.updatedAt = Instant.now();
    }

    public static Order restore(UUID id, UUID investorId, UUID productId, Integer quantity,
                                BigDecimal unitPrice, BigDecimal totalAmount, OrderStatus status,
                                Instant createdAt, Instant updatedAt) {
        Order order = new Order(investorId, productId, quantity, unitPrice);
        order.id = id;
        order.createdAt = createdAt;
        order.updatedAt = updatedAt;
        order.status = status;
        order.totalAmount = totalAmount;
        return order;
    }

    public UUID getId() { return id; }
    public UUID getInvestorId() { return investorId; }
    public UUID getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}