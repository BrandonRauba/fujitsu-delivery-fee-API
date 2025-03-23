package com.example.feecalculator.service;

import com.example.feecalculator.model.WeatherData;
import com.example.feecalculator.repository.WeatherRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryFeeServiceTest {

    private final WeatherRepository weatherRepository = mock(WeatherRepository.class);
    private final DeliveryFeeService feeService = new DeliveryFeeService(weatherRepository);

    @Test
    void testCalculateDeliveryFeeWithoutExtraFees() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setAirTemperature(5);
        mockWeatherData.setWindSpeed(10);
        mockWeatherData.setWeatherPhenomenon("clear");

        when(weatherRepository.findTopByStationNameIgnoreCaseOrderByTimestampDesc("Tallinn-Harku"))
                .thenReturn(Optional.of(mockWeatherData));

        Double fee = feeService.calculateDeliveryFee("Tallinn-Harku", "Car");
        assertEquals(4.0, fee);
    }

    @Test
    void testCalculateDeliveryFeeWithExtraFees() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setAirTemperature(-2.1);
        mockWeatherData.setWindSpeed(4.7);
        mockWeatherData.setWeatherPhenomenon("Light snow shower");

        when(weatherRepository.findTopByStationNameIgnoreCaseOrderByTimestampDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(mockWeatherData));

        Double fee = feeService.calculateDeliveryFee("Tartu-Tõravere", "Bike");
        assertEquals(4.0, fee);
    }

    @Test
    void testCalculateExtraFees() {
        Double extraFee = feeService.calculateExtraFees("Bike", -2.1, 4.7, "Light snow shower");
        assertEquals(1.5, extraFee);
    }

    @Test
    void testWeatherConditionsAreTooExtremeForDelivery() {
        assertThrows(RuntimeException.class, () -> feeService.calculateExtraFees("Bike", -8, 11, "Glaze"));
    }

    @Test
    void testWindSpeedIsTooHighForDelivery() {
        assertThrows(RuntimeException.class, () -> feeService.calculateExtraFees("Bike", 5, 25, "Light rain"));
    }

    @Test
    void testCalculateDeliveryFeeNoWeatherData() {
        when(weatherRepository.findTopByStationNameIgnoreCaseOrderByTimestampDesc("Tallinn-Harku"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> feeService.calculateDeliveryFee("Tallinn-Harku", "Car"));
    }

    @Test
    void testInvalidCityOrVehicle() {
        assertThrows(RuntimeException.class, () -> feeService.calculateDeliveryFee("Saaremaa", "Car"));
    }
}