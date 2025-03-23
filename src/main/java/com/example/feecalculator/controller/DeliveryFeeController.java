package com.example.feecalculator.controller;

import com.example.feecalculator.documentation.DeliveryFeeResponse;
import com.example.feecalculator.documentation.ErrorResponse;
import com.example.feecalculator.service.DeliveryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery-fee")
@Tag(name = "Delivery fee", description = "API for calculating delivery fees.")
public class DeliveryFeeController {
    private final DeliveryFeeService feeService;

    @Autowired
    public DeliveryFeeController(DeliveryFeeService feeService) {
        this.feeService = feeService;
    }

    @Operation(summary = "Get delivery fees",
            description = "Calculates the delivery fee based on city and vehicle type, considering weather conditions.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Delivery fee calculated successfully",
                            content = @Content(mediaType = "text",
                                    schema = @Schema(implementation = DeliveryFeeResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping
    public ResponseEntity<?> getDeliveryFee(
            @Parameter(description = "City name (Tallinn, Tartu, Pärnu)") @RequestParam String city,
            @Parameter(description = "Vehicle type (Car, Scooter, Bike)") @RequestParam String vehicleType
    ) {
        if (city.equals("Tallinn"))
            city = "Tallinn-Harku";
        else if (city.equals("Tartu"))
            city = "Tartu-Tõravere";

        try {
            Double fee = feeService.calculateDeliveryFee(city, vehicleType);
            return ResponseEntity.ok(Map.of("deliveryFee", fee));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
