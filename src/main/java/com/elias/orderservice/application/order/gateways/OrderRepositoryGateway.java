package com.elias.orderservice.application.order.gateways;

import com.elias.orderservice.domain.order.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryGateway {
    Order save(Order order);
    Optional<Order> findById(UUID id);
}