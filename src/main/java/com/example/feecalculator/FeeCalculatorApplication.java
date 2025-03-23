package com.example.feecalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FeeCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeeCalculatorApplication.class, args);
	}
}
