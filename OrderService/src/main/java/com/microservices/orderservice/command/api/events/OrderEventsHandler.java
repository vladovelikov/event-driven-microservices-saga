package com.microservices.orderservice.command.api.events;

import com.microservices.CommonService.events.OrderCompletedEvent;
import com.microservices.orderservice.command.api.entity.Order;
import com.microservices.orderservice.command.api.repository.OrderRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsHandler {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderEventsHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        Order order = new Order();
        BeanUtils.copyProperties(orderCreatedEvent, order);
        this.orderRepository.save(order);
    }

    @EventHandler
    public void on(OrderCompletedEvent orderCompletedEvent) {
        Order order = this.orderRepository.findById(orderCompletedEvent.getOrderId()).get();
        order.setOrderStatus(orderCompletedEvent.getOrderStatus());
        this.orderRepository.save(order);
    }

    @EventHandler
    public void on(OrderCanceledEvent orderCanceledEvent) {
        Order order = this.orderRepository.findById(orderCanceledEvent.getOrderid()).get();
        order.setOrderStatus(orderCanceledEvent.getOrderStatus());
        this.orderRepository.save(order);
    }
}
