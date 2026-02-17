package com.elias.orderservice.infrastructure.order.persistence.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(
        UUID investorId,
        UUID productId,
        BigDecimal quantity,
        BigDecimal unitPrice
) {}