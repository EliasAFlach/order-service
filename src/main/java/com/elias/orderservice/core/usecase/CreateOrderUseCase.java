package com.elias.orderservice.core.usecase;

import com.elias.investcommon.event.order.OrderCreatedEvent;
import com.elias.orderservice.core.domain.Order;
import com.elias.orderservice.core.ports.outgoing.OrderEventPublisherPort;
import com.elias.orderservice.core.ports.outgoing.OrderRepositoryPort;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    public CreateOrderUseCase(OrderRepositoryPort orderRepository, OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public UUID execute(UUID investorId, UUID productId, Integer quantity, BigDecimal unitPrice) {
        var order = Order.create(investorId, productId, quantity, unitPrice);

        var savedOrder = orderRepository.save(order);

        var event = new OrderCreatedEvent(
                null,
                null,
                savedOrder.getId(),
                savedOrder.getInvestorId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getUnitPrice(),
                savedOrder.getTotalAmount()
        );

        eventPublisher.publish(event);

        return savedOrder.getId();
    }
}