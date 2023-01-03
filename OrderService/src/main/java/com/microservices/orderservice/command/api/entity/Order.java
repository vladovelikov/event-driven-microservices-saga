package com.microservices.orderservice.command.api.entity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String orderId;

    private String productId;

    private String userId;

    private String addressId;

    private Integer quantity;

    public String orderStatus;

}
