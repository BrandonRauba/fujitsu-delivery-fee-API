package com.example.feecalculator.repository;

import com.example.feecalculator.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findTopByStationNameIgnoreCaseOrderByTimestampDesc(String stationName);
}
