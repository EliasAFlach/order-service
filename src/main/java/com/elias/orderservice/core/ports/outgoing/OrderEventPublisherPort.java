package com.elias.orderservice.core.ports.outgoing;

import com.elias.investcommon.event.order.OrderCreatedEvent;

public interface OrderEventPublisherPort {
    void publish(OrderCreatedEvent event);
}