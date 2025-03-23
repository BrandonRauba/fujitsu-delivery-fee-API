package com.example.feecalculator.service;

import com.example.feecalculator.model.WeatherData;
import com.example.feecalculator.repository.WeatherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class DeliveryFeeService {
    private final WeatherRepository weatherRepository;

    public DeliveryFeeService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    //Could insert rows into DeliveryFeeRepository if database was stable. For now keep the list hardcoded in the code.
    private static final Map<String, Map<String, Double>> BASE_FEES = Map.of(
            "Tallinn-Harku", Map.of("Car", 4.0, "Scooter", 3.5, "Bike", 3.0),
            "Tartu-Tõravere", Map.of("Car", 3.5, "Scooter", 3.0, "Bike", 2.5),
            "Pärnu", Map.of("Car", 3.0, "Scooter", 2.5, "Bike", 2.0)
    );

    public double calculateExtraFees(String vehicleType, double airTemperature, double windSpeed, String weatherPhenomenon) {
        double extraFee = 0;

        if ((vehicleType.equalsIgnoreCase("Scooter") || vehicleType.equalsIgnoreCase("Bike"))) {
            if (airTemperature < -10) {
                extraFee += 1.0;
            } else if (airTemperature >= -10 && airTemperature < 0) {
                extraFee += 0.5;
            }
        }

        if (vehicleType.equalsIgnoreCase("Bike")) {
            if (windSpeed > 20) {
                throw new RuntimeException("Usage of selected vehicle type is forbidden due to high wind speeds.");
            } else if (windSpeed >= 10 && windSpeed <= 20) {
                extraFee += 0.5;
            }
        }

        if (vehicleType.equalsIgnoreCase("Scooter") || vehicleType.equalsIgnoreCase("Bike")) {
            if (weatherPhenomenon != null) {
                String lowerCasePhenomenon = weatherPhenomenon.toLowerCase();
                if (lowerCasePhenomenon.contains("snow") || lowerCasePhenomenon.contains("sleet")) {
                    extraFee += 1.0;
                } else if (lowerCasePhenomenon.contains("rain")) {
                    extraFee += 0.5;
                } else if (lowerCasePhenomenon.contains("glaze") ||
                        lowerCasePhenomenon.contains("hail") ||
                        lowerCasePhenomenon.contains("thunder")) {
                    throw new RuntimeException("Usage of selected vehicle type is forbidden due to extreme weather conditions.");
                }
            }
        }
        return extraFee;
    }

    public Double calculateDeliveryFee(String city, String vehicleType) {
        if (!BASE_FEES.containsKey(city) || !BASE_FEES.get(city).containsKey(vehicleType)) {
            throw new RuntimeException("Invalid city or vehicle type!");
        }

        WeatherData weatherData = weatherRepository.findTopByStationNameIgnoreCaseOrderByTimestampDesc(city)
                    .orElseThrow(() -> new RuntimeException("No recent weather data available for " + city));


        double totalFee = BASE_FEES.get(city).get(vehicleType);

        totalFee += calculateExtraFees(
                vehicleType,
                weatherData.getAirTemperature(),
                weatherData.getWindSpeed(),
                weatherData.getWeatherPhenomenon());

        return totalFee;
    }
}
