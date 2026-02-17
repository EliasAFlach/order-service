package com.elias.orderservice.infrastructure.order.controller;

import com.elias.orderservice.application.order.usecases.CreateOrderUseCase;
import com.elias.orderservice.infrastructure.order.persistence.request.CreateOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
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
}
