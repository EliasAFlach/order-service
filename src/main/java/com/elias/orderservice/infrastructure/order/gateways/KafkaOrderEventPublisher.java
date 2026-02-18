package com.elias.orderservice.infrastructure.order.gateways;

import com.elias.investcommon.event.order.OrderCreatedEvent;
import com.elias.investcommon.event.order.OrderValidatedEvent;
import com.elias.investcommon.event.risk.RiskCheckRequestedEvent;
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

    @Value("${app.kafka.topics.order-created}")
    private String validateTopic;

    @Value("${app.kafka.topics.risk-check-requested}")
    private String riskCheckRequestedTopic;

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

    @Override
    public void publishOrderValidated(Order order, UUID correlationId) {
        try {
            OrderValidatedEvent event = OrderValidatedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .occurredOn(Instant.now())
                    .schemaVersion("1")
                    .correlationId(correlationId)
                    .causationId(order.getId())
                    .orderId(order.getId())
                    .investorId(order.getInvestorId())
                    .build();

            OutboxEntity outbox = new OutboxEntity();
            outbox.setTopic(validateTopic);
            outbox.setEventKey(order.getId().toString());
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outbox.setCreatedAt(Instant.now());

            outboxRepository.save(outbox);

            log.info("[OUTBOX] orderValidated salvo. orderId={} correlationId={}",
                    order.getId(), correlationId);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao publicar OrderValidatedEvent", e);
        }
    }

    @Override
    public void publishRiskCheckRequested(Order order, UUID correlationId) {
        try {
            RiskCheckRequestedEvent event = RiskCheckRequestedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .occurredOn(Instant.now())
                    .schemaVersion("1")
                    .correlationId(correlationId)
                    .causationId(order.getId())
                    .orderId(order.getId())
                    .investorId(order.getInvestorId())
                    .productId(order.getProductId())
                    .quantity(order.getQuantity())
                    .unitPrice(order.getUnitPrice())
                    .totalAmount(order.getTotalAmount())
                    .build();

            OutboxEntity outbox = new OutboxEntity();
            outbox.setTopic(riskCheckRequestedTopic);
            outbox.setEventKey(order.getId().toString());
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outbox.setCreatedAt(Instant.now());

            outboxRepository.save(outbox);

            log.info("[OUTBOX] riskCheckRequested salvo. orderId={} eventId={} correlationId={}",
                    order.getId(), event.getEventId(), correlationId);

        } catch (Exception e) {
            log.error("[OUTBOX] Falha ao salvar riskCheckRequested. orderId={} correlationId={}",
                    order.getId(), correlationId, e);
            throw new RuntimeException("Erro ao publicar RiskCheckRequestedEvent", e);
        }
    }
}
