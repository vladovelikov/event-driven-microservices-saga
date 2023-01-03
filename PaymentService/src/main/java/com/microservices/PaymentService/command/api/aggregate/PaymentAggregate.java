package com.microservices.PaymentService.command.api.aggregate;

import com.microservices.CommonService.commands.CancelPaymentCommand;
import com.microservices.CommonService.commands.ValidatePaymentCommand;
import com.microservices.CommonService.events.PaymentCanceledEvent;
import com.microservices.CommonService.events.PaymentProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@Slf4j
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;

    private String orderId;

    private String paymentStatus;

    public PaymentAggregate() {

    }

    @CommandHandler
    public PaymentAggregate(ValidatePaymentCommand validatePaymentCommand) {
        //validate the payment details
        // if everything is processed successfully, we can publish the payment processed event

        log.info("Executing ValidatePaymentCommand for Order Id: {} and Payment Id: {}",
                validatePaymentCommand.getOrderId(),
                validatePaymentCommand.getPaymentId());

        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
                validatePaymentCommand.getPaymentId(), validatePaymentCommand.getOrderId()
        );

        AggregateLifecycle.apply(paymentProcessedEvent);

        log.info("PaymentProcessedEvent Applied");

    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.orderId = paymentProcessedEvent.getOrderId();
    }

    @CommandHandler
    public void handle(CancelPaymentCommand cancelPaymentCommand) {
        log.info("Executing CancelPaymentCommand for Order Id: {} and Payment Id: {}",
                cancelPaymentCommand.getOrderId(),
                cancelPaymentCommand.getPaymentId());

        PaymentCanceledEvent paymentCanceledEvent = new PaymentCanceledEvent();
        BeanUtils.copyProperties(cancelPaymentCommand, paymentCanceledEvent);

        AggregateLifecycle.apply(paymentCanceledEvent);

        log.info("PaymentCanceledEvent Applied");
    }

    @EventSourcingHandler
    public void on(PaymentCanceledEvent paymentCanceledEvent) {
        this.paymentStatus = paymentCanceledEvent.getPaymentStatus();
    }
}
