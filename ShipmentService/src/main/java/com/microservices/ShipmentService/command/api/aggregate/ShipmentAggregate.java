package com.microservices.ShipmentService.command.api.aggregate;

import com.microservices.CommonService.commands.ShipOrderCommand;
import com.microservices.CommonService.events.OrderShippedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Slf4j
public class ShipmentAggregate {

    @AggregateIdentifier
    private String shipmentId;

    private String orderId;

    private String shipmentStatus;

    public ShipmentAggregate() {
    }

    @CommandHandler
    public ShipmentAggregate(ShipOrderCommand shipOrderCommand) {
        //validate the ShipOrderCommand
        //publish the Order Shipped Event

        OrderShippedEvent orderShippedEvent = OrderShippedEvent.builder()
                .shipmentId(shipOrderCommand.getShipmentId())
                .orderId(shipOrderCommand.getOrderId())
                .shipmentStatus("IN PROGRESS")
                .build();

        AggregateLifecycle.apply(orderShippedEvent);

        log.info("OrderShippedEvent Applied");
    }

    @EventSourcingHandler
    public void on(OrderShippedEvent orderShippedEvent) {
        this.shipmentId = orderShippedEvent.getShipmentId();
        this.orderId = orderShippedEvent.getOrderId();
        this.shipmentStatus = orderShippedEvent.getShipmentStatus();
    }
}
