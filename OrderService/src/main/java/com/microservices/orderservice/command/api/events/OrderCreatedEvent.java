package com.microservices.orderservice.command.api.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private String orderId;

    private String productId;

    private String userId;

    private String addressId;

    private Integer quantity;

    private String orderStatus;
}
