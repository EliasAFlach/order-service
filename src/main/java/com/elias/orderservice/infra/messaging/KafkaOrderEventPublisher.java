package com.elias.orderservice.infra.messaging;

import com.elias.investcommon.event.order.OrderCreatedEvent;
import com.elias.orderservice.core.ports.outgoing.OrderEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.order-created}")
    private String topic;

    @Override
    public void publish(OrderCreatedEvent event) {
        String key = event.getOrderId().toString();

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("[KAFKA] Erro ao enviar evento: " + ex.getMessage());
                    } else {
                        System.out.println("[KAFKA] Sucesso. Offset: " + result.getRecordMetadata().offset());
                    }
                });
    }
}