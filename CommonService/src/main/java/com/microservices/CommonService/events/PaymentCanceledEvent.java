package com.microservices.CommonService.events;

import lombok.Data;

@Data
public class PaymentCanceledEvent {

    private String paymentId;
    private String orderId;
    private String paymentStatus;

}
