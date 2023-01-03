package com.microservices.ShipmentService.command.api.repository;

import com.microservices.ShipmentService.command.api.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
}
