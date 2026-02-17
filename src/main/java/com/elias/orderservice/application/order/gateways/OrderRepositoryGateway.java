package com.elias.orderservice.application.order.gateways;

import com.elias.orderservice.domain.order.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryGateway {
    Order save(Order order, String idempotencyKey);
    Optional<Order> findById(UUID id);
    Optional<Order> findByIdempotencyKey(String idempotencyKey);
}
