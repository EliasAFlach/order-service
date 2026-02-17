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
    public UUID execute(
            String idempotencyKey,
            String correlationId,
            UUID investorId,
            UUID productId,
            BigDecimal quantity,
            BigDecimal unitPrice
    ) {
        UUID corr = parseOrGenerateCorrelationId(correlationId);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = orderRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return existing.get().getId();
            }
        }

        Order order = Order.create(investorId, productId, quantity, unitPrice);

        orderRepository.save(order, idempotencyKey);

        orderEventPublisher.publishOrderCreated(order, corr);

        return order.getId();
    }

    private UUID parseOrGenerateCorrelationId(String correlationId) {
        try {
            return (correlationId == null || correlationId.isBlank())
                    ? UUID.randomUUID()
                    : UUID.fromString(correlationId);
        } catch (IllegalArgumentException ex) {
            return UUID.randomUUID();
        }
    }
}
