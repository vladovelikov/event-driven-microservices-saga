package com.microservices.orderservice.command.api.saga;

import com.microservices.CommonService.commands.*;
import com.microservices.CommonService.entity.User;
import com.microservices.CommonService.events.*;
import com.microservices.CommonService.queries.GetUserPaymentDetailsQuery;
import com.microservices.orderservice.command.api.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
@Slf4j
public class OrderProcessingSaga {

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;

    public OrderProcessingSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    private void handle(OrderCreatedEvent orderCreatedEvent) {
        log.info("OrderCreatedEvent in Saga for Order Id: {}", orderCreatedEvent.getOrderId());

        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery = new GetUserPaymentDetailsQuery(orderCreatedEvent.getUserId());

        User user = null;

        try {
            user = queryGateway.query(
                    getUserPaymentDetailsQuery,
                    ResponseTypes.instanceOf(User.class)
                ).join();
        } catch (Exception e) {
            log.error(e.getMessage());

            //Start the Compensating transaction
            cancelOrderCommand(orderCreatedEvent.getOrderId());
        }

        ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .paymentId(UUID.randomUUID().toString())
                .cardDetails(user.getCardDetails())
                .build();

        this.commandGateway.sendAndWait(validatePaymentCommand);
    }

    private void cancelOrderCommand(String orderId) {
        CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(orderId);
        this.commandGateway.send(cancelOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    private void handle(PaymentProcessedEvent paymentProcessedEvent) {
        log.info("PaymentProcessedEvent in Saga for Order Id: {}", paymentProcessedEvent.getOrderId());

        try {
            ShipOrderCommand shipOrderCommand = ShipOrderCommand.builder()
                    .shipmentId(UUID.randomUUID().toString())
                    .orderId(paymentProcessedEvent.getOrderId())
                    .build();

            this.commandGateway.send(shipOrderCommand);
        } catch (Exception e) {
            log.error(e.getMessage());
            //Start the compensating transaction
            cancelPaymentCommand(paymentProcessedEvent);
        }
    }

    private void cancelPaymentCommand(PaymentProcessedEvent paymentProcessedEvent) {
        CancelPaymentCommand cancelPaymentCommand = new CancelPaymentCommand(
                paymentProcessedEvent.getPaymentId(), paymentProcessedEvent.getOrderId());
        this.commandGateway.send(cancelPaymentCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderShippedEvent orderShippedEvent) {
        log.info("OrderShippedEvent in Saga for Order Id: {}", orderShippedEvent.getOrderId());

        try {
            CompleteOrderCommand completeOrderCommand = CompleteOrderCommand.builder()
                    .orderId(orderShippedEvent.getOrderId())
                    .orderStatus("APPROVED")
                    .build();

            this.commandGateway.send(completeOrderCommand);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCompletedEvent orderCompletedEvent) {
        log.info("OrderCompletedEvent in Saga for Order Id: {}", orderCompletedEvent.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCanceledEvent orderCanceledEvent) {
        log.info("OrderCanceledEvent in Saga for Order Id: {}", orderCanceledEvent.getOrderid());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCanceledEvent paymentCanceledEvent) {
        log.info("PaymentCanceledEvent in Saga for Order Id: {}", paymentCanceledEvent.getOrderId());
        cancelOrderCommand(paymentCanceledEvent.getOrderId());
    }

}
