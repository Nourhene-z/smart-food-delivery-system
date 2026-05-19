package com.designpatterns.fooddelivery.repository;

import com.designpatterns.fooddelivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Delivery entity.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findByDeliveryStatus(Delivery.DeliveryStatus status);

    List<Delivery> findByDriverName(String driverName);
}
