package com.elias.orderservice.infrastructure.outbox.scheduler;

import com.elias.orderservice.infrastructure.outbox.persistence.OutboxEntity;
import com.elias.orderservice.infrastructure.outbox.persistence.SpringDataOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final SpringDataOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${app.outbox.relay-delay-ms:5000}")
    @Transactional
    public void processOutboxMessages() {
        List<OutboxEntity> pending = outboxRepository.findTop100ByProcessedAtIsNullOrderByCreatedAtAsc();

        if (pending.isEmpty()) {
            return;
        }

        for (OutboxEntity msg : pending) {
            msg.setAttempts(msg.getAttempts() == null ? 1 : msg.getAttempts() + 1);
            outboxRepository.save(msg);

            kafkaTemplate.send(msg.getTopic(), msg.getEventKey(), msg.getPayload())
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            markProcessed(msg.getId());
                            log.info("[RELAY] Enviado e marcado processado. outboxId={} key={} topic={}",
                                    msg.getId(), msg.getEventKey(), msg.getTopic());
                        } else {
                            markFailed(msg.getId(), ex);
                            log.warn("[RELAY] Falha ao enviar. outboxId={} key={} attempts={}",
                                    msg.getId(), msg.getEventKey(), msg.getAttempts(), ex);
                        }
                    });
        }
    }

    @Transactional
    protected void markProcessed(java.util.UUID outboxId) {
        outboxRepository.findById(outboxId).ifPresent(entity -> {
            entity.setProcessedAt(Instant.now());
            entity.setLastError(null);
            outboxRepository.save(entity);
        });
    }

    @Transactional
    protected void markFailed(java.util.UUID outboxId, Throwable ex) {
        outboxRepository.findById(outboxId).ifPresent(entity -> {
            entity.setLastError(ex.getMessage());
            outboxRepository.save(entity);
        });
    }
}
