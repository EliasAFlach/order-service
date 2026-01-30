package com.elias.orderservice.core.ports.outgoing;

import com.elias.orderservice.core.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(UUID id);
}