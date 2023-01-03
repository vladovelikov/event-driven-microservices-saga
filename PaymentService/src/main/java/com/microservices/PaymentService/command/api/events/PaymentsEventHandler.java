package com.microservices.PaymentService.command.api.events;

import com.microservices.CommonService.events.PaymentCanceledEvent;
import com.microservices.CommonService.events.PaymentProcessedEvent;
import com.microservices.PaymentService.command.api.entity.Payment;
import com.microservices.PaymentService.command.api.repository.PaymentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PaymentsEventHandler {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentsEventHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        Payment payment = Payment.builder()
                .paymentId(paymentProcessedEvent.getPaymentId())
                .orderId(paymentProcessedEvent.getOrderId())
                .paymentStatus("COMPLETED")
                .date(new Date())
                .build();

        this.paymentRepository.save(payment);
    }

    @EventHandler
    public void on(PaymentCanceledEvent paymentCanceledEvent) {
        Payment payment = this.paymentRepository.findById(paymentCanceledEvent.getPaymentId()).get();
        payment.setPaymentStatus(payment.getPaymentStatus());

        this.paymentRepository.save(payment);
    }
}
