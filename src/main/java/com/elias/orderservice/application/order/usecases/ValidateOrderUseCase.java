package com.elias.orderservice.application.order.usecases;

import com.elias.orderservice.application.order.gateways.OrderEventPublisherGateway;
import com.elias.orderservice.application.order.gateways.OrderRepositoryGateway;
import com.elias.orderservice.domain.order.Order;

import java.util.UUID;

public class ValidateOrderUseCase {

    private final OrderRepositoryGateway orderRepository;
    private final OrderEventPublisherGateway eventPublisher;

    public ValidateOrderUseCase(
            OrderRepositoryGateway orderRepository,
            OrderEventPublisherGateway eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(UUID orderId, UUID correlationId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.markAsValidated();

        orderRepository.save(order, null);

        eventPublisher.publishOrderValidated(order, correlationId);
    }
}
