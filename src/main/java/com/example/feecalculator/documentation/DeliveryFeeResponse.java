package com.example.feecalculator.documentation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response for delivery fee calculation")
public class DeliveryFeeResponse {
    @Schema(description = "deliveryFee", example = "4.0")
    private Double deliveryFee;

    public DeliveryFeeResponse(boolean correct, Double deliveryFee, String city, String vehicleType) {
        this.deliveryFee = deliveryFee;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }
}