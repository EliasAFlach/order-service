package com.elias.orderservice.infrastructure.config;

import com.elias.orderservice.application.order.gateways.OrderEventPublisherGateway;
import com.elias.orderservice.application.order.gateways.OrderRepositoryGateway;
import com.elias.orderservice.application.order.usecases.CreateOrderUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepositoryGateway orderRepository,
            OrderEventPublisherGateway orderEventPublisher
    ) {
        return new CreateOrderUseCase(orderRepository, orderEventPublisher);
    }
}