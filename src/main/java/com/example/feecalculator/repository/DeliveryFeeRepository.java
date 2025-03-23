package com.example.feecalculator.repository;

import com.example.feecalculator.model.DeliveryFeeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryFeeRepository extends JpaRepository<DeliveryFeeConfig, Long> {
    Optional<DeliveryFeeConfig> findByRegionAndVehicleType(String region, String vehicleType);
}
