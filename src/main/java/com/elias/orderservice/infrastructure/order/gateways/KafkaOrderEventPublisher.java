package com.elias.orderservice.infrastructure.order.gateways;

import com.elias.investcommon.event.order.OrderCreatedEvent;
import com.elias.orderservice.application.order.gateways.OrderEventPublisherGateway;
import com.elias.orderservice.domain.order.Order;
import com.elias.orderservice.infrastructure.outbox.persistence.OutboxEntity;
import com.elias.orderservice.infrastructure.outbox.persistence.SpringDataOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisherGateway {

    private final SpringDataOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.order-created}")
    private String topic;

    @Override
    public void publishOrderCreated(Order order, UUID correlationId) {
        try {
            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .occurredOn(Instant.now())
                    .schemaVersion("1")
                    .correlationId(correlationId)
                    .causationId(null)
                    .orderId(order.getId())
                    .investorId(order.getInvestorId())
                    .productId(order.getProductId())
                    .quantity(order.getQuantity())
                    .unitPrice(order.getUnitPrice())
                    .totalAmount(order.getTotalAmount())
                    .build();

            OutboxEntity outbox = new OutboxEntity();
            outbox.setTopic(topic);
            outbox.setEventKey(order.getId().toString());
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outbox.setCreatedAt(Instant.now());

            outboxRepository.save(outbox);

            log.info("[OUTBOX] orderCreated salvo. orderId={} eventId={} correlationId={}",
                    order.getId(), event.getEventId(), event.getCorrelationId());

        } catch (Exception e) {
            log.error("[OUTBOX] Falha ao salvar evento orderCreated. orderId={} correlationId={}",
                    order.getId(), correlationId, e);
            throw new RuntimeException("Erro ao serializar/salvar evento no Outbox", e);
        }
    }
}
