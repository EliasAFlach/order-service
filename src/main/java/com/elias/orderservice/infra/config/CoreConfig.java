package com.elias.orderservice.infra.config;

import com.elias.orderservice.core.ports.outgoing.OrderEventPublisherPort;
import com.elias.orderservice.core.ports.outgoing.OrderRepositoryPort;
import com.elias.orderservice.core.usecase.CreateOrderUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepositoryPort orderRepository,
            OrderEventPublisherPort orderEventPublisher
    ) {
        return new CreateOrderUseCase(orderRepository, orderEventPublisher);
    }
}
