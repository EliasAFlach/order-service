package com.elias.orderservice.application.order.gateways;

import com.elias.orderservice.domain.order.Order;

import java.util.UUID;

public interface OrderEventPublisherGateway {
    void publishOrderCreated(Order order, UUID correlationId);
}
