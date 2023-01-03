package com.microservices.ShipmentService.command.api.events;

import com.microservices.CommonService.events.OrderShippedEvent;
import com.microservices.ShipmentService.command.api.entity.Shipment;
import com.microservices.ShipmentService.command.api.repository.ShipmentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShipmentsEventHandler {

    private final ShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentsEventHandler(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @EventHandler
    public void on(OrderShippedEvent orderShippedEvent) {
        Shipment shipment = new Shipment();
        BeanUtils.copyProperties(orderShippedEvent, shipment);
        this.shipmentRepository.save(shipment);
    }

}
