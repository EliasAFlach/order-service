package com.elias.orderservice.application.order.usecases;

import com.elias.orderservice.application.order.gateways.OrderEventPublisherGateway;
import com.elias.orderservice.application.order.gateways.OrderRepositoryGateway;
import com.elias.orderservice.domain.order.Order;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateOrderUseCase {

    private final OrderRepositoryGateway orderRepository;
    private final OrderEventPublisherGateway orderEventPublisher;

    public CreateOrderUseCase(OrderRepositoryGateway orderRepository, OrderEventPublisherGateway orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional
    public UUID execute(UUID investorId, UUID productId, BigDecimal quantity, BigDecimal unitPrice) {
        Order order = Order.create(investorId, productId, quantity, unitPrice);

        orderRepository.save(order);

        orderEventPublisher.publishOrderCreated(order);

        return order.getId();
    }
}