package com.elias.orderservice.infra.api;

import com.elias.orderservice.core.usecase.CreateOrderUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateOrderRequest request) {
        var id = createOrderUseCase.execute(
                request.investorId(),
                request.productId(),
                request.quantity(),
                request.unitPrice()
        );

        return ResponseEntity.created(URI.create("/api/v1/orders/" + id)).build();
    }

    public record CreateOrderRequest(
            UUID investorId,
            UUID productId,
            Integer quantity,
            BigDecimal unitPrice
    ) {}
}