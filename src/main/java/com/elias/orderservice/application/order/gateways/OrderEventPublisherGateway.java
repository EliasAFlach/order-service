package com.elias.orderservice.application.order.gateways;

import com.elias.orderservice.domain.order.Order;

public interface OrderEventPublisherGateway {
    void publishOrderCreated(Order order);
}