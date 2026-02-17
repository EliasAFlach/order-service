package com.elias.orderservice.infrastructure.outbox.scheduler;

import com.elias.orderservice.infrastructure.outbox.persistence.OutboxEntity;
import com.elias.orderservice.infrastructure.outbox.persistence.SpringDataOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final SpringDataOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxMessages() {
        List<OutboxEntity> pendingMessages = outboxRepository.findTop100ByProcessedAtIsNullOrderByCreatedAtAsc();

        for (OutboxEntity message : pendingMessages) {
            try {
                kafkaTemplate.send(message.getTopic(), message.getEventKey(), message.getPayload()).get();

                message.setProcessedAt(Instant.now());
                outboxRepository.save(message);

                System.out.println("[RELAY] Mensagem enviada ao Kafka e marcada como processada: " + message.getEventKey());
            } catch (Exception e) {
                System.err.println("[RELAY] Falha ao enviar mensagem: " + message.getId() + ". Tentará novamente no próximo ciclo.");
            }
        }
    }
}