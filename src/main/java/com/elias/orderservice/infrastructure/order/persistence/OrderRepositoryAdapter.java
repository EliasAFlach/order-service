package com.elias.orderservice.infrastructure.order.persistence;

import com.elias.investcommon.domain.OrderStatus;
import com.elias.orderservice.domain.order.Order;
import com.elias.orderservice.application.order.gateways.OrderRepositoryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryGateway {

    private final SpringDataOrderRepository springRepository;

    @Override
    public Order save(Order order, String idempotencyKey) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setInvestorId(order.getInvestorId());
        entity.setProductId(order.getProductId());
        entity.setQuantity(order.getQuantity());
        entity.setUnitPrice(order.getUnitPrice());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus().name());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setIdempotencyKey(idempotencyKey);

        try {
            springRepository.save(entity);
            return order;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                return findByIdempotencyKey(idempotencyKey)
                        .orElseThrow(() -> e);
            }
            throw e;
        }
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return springRepository.findById(id)
                .map(entity -> Order.restore(
                        entity.getId(),
                        entity.getInvestorId(),
                        entity.getProductId(),
                        entity.getQuantity(),
                        entity.getUnitPrice(),
                        entity.getTotalAmount(),
                        OrderStatus.valueOf(entity.getStatus()),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                ));
    }

    @Override
    public Optional<Order> findByIdempotencyKey(String idempotencyKey) {
        return springRepository.findByIdempotencyKey(idempotencyKey)
                .map(entity -> Order.restore(
                        entity.getId(),
                        entity.getInvestorId(),
                        entity.getProductId(),
                        entity.getQuantity(),
                        entity.getUnitPrice(),
                        entity.getTotalAmount(),
                        OrderStatus.valueOf(entity.getStatus()),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                ));
    }
}
