package com.elias.orderservice.infrastructure.config;

import com.elias.orderservice.application.order.gateways.OrderEventPublisherGateway;
import com.elias.orderservice.application.order.gateways.OrderRepositoryGateway;
import com.elias.orderservice.application.order.usecases.CreateOrderUseCase;
import com.elias.orderservice.application.order.usecases.RequestRiskCheckUseCase;
import com.elias.orderservice.application.order.usecases.ValidateOrderUseCase;
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

    @Bean
    public ValidateOrderUseCase validateOrderUseCase(
            OrderRepositoryGateway orderRepository,
            OrderEventPublisherGateway eventPublisher
    ) {
        return new ValidateOrderUseCase(orderRepository, eventPublisher);
    }

    @Bean
    public RequestRiskCheckUseCase requestRiskCheckUseCase(
            OrderRepositoryGateway orderRepository,
            OrderEventPublisherGateway eventPublisher
    ) {
        return new RequestRiskCheckUseCase(orderRepository, eventPublisher);
    }
}