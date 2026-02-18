package com.elias.orderservice.infrastructure.order.controller;

import com.elias.orderservice.application.order.usecases.CreateOrderUseCase;
import com.elias.orderservice.application.order.usecases.RequestRiskCheckUseCase;
import com.elias.orderservice.application.order.usecases.ValidateOrderUseCase;
import com.elias.orderservice.infrastructure.order.persistence.request.CreateOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ValidateOrderUseCase validateOrderUseCase;
    private final RequestRiskCheckUseCase  requestRiskCheckUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           ValidateOrderUseCase validateOrderUseCase,
                           RequestRiskCheckUseCase requestRiskCheckUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.validateOrderUseCase = validateOrderUseCase;
        this.requestRiskCheckUseCase = requestRiskCheckUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestBody CreateOrderRequest request
    ) {
        var id = createOrderUseCase.execute(
                idempotencyKey,
                correlationId,
                request.investorId(),
                request.productId(),
                request.quantity(),
                request.unitPrice()
        );

        return ResponseEntity.created(URI.create("/api/v1/orders/" + id)).build();
    }

    @PostMapping("/{orderId}/validate")
    public ResponseEntity<Void> validate(
            @PathVariable UUID orderId,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        UUID corr = correlationId != null
                ? UUID.fromString(correlationId)
                : UUID.randomUUID();

        validateOrderUseCase.execute(orderId, corr);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{orderId}/risk/request")
    public ResponseEntity<Void> requestRisk(
            @PathVariable UUID orderId,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        UUID corr = (correlationId == null || correlationId.isBlank())
                ? UUID.randomUUID()
                : UUID.fromString(correlationId);

        requestRiskCheckUseCase.execute(orderId, corr);

        return ResponseEntity.accepted().build();
    }
}
