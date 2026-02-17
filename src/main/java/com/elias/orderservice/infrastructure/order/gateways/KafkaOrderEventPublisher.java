package com.elias.orderservice.infrastructure.order.gateways;

import com.elias.investcommon.event.order.OrderCreatedEvent;
import com.elias.orderservice.application.order.gateways.OrderEventPublisherGateway;
import com.elias.orderservice.domain.order.Order;
import com.elias.orderservice.infrastructure.outbox.persistence.OutboxEntity;
import com.elias.orderservice.infrastructure.outbox.persistence.SpringDataOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisherGateway {

    private final SpringDataOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.order-created}")
    private String topic;

    @Override
    public void publishOrderCreated(Order order) {
        try {
            OrderCreatedEvent event = OrderCreatedEvent.builder()
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

            System.out.println("[OUTBOX] Evento salvo no banco de dados com sucesso!");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar evento para o Outbox", e);
        }
    }
}