package com.microservices.orderservice.command.api.aggregate;

import com.microservices.CommonService.commands.CancelOrderCommand;
import com.microservices.CommonService.commands.CompleteOrderCommand;
import com.microservices.CommonService.events.OrderCompletedEvent;
import com.microservices.orderservice.command.api.command.CreateOrderCommand;
import com.microservices.orderservice.command.api.events.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;

    private String productId;

    private String userId;

    private String addressId;

    private Integer quantity;

    private String orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        //validate the command

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        this.orderId = orderCreatedEvent.getOrderId();
        this.productId = orderCreatedEvent.getProductId();
        this.userId = orderCreatedEvent.getUserId();
        this.addressId = orderCreatedEvent.getAddressId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }

    @CommandHandler
    public void handle(CompleteOrderCommand completeOrderCommand) {
        //validate the command
        //if everything is fine, publish the OrderCompletedEvent

        OrderCompletedEvent orderCompletedEvent = OrderCompletedEvent.builder()
                .orderId(completeOrderCommand.getOrderId())
                .orderStatus(completeOrderCommand.getOrderStatus())
                .build();

        AggregateLifecycle.apply(orderCompletedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent orderCompletedEvent) {
        this.orderStatus = orderCompletedEvent.getOrderStatus();
    }

    public void handle(CancelOrderCommand cancelOrderCommand) {
        OrderCanceledEvent orderCanceledEvent = new OrderCanceledEvent();
        BeanUtils.copyProperties(cancelOrderCommand, orderCanceledEvent);

        AggregateLifecycle.apply(orderCanceledEvent);
    }

    @EventSourcingHandler
    public void on(OrderCanceledEvent orderCanceledEvent) {
        this.orderStatus = orderCanceledEvent.getOrderStatus();
    }

}
