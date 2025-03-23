package com.example.feecalculator.service;

import com.example.feecalculator.model.WeatherData;
import com.example.feecalculator.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Component
public class WeatherDataFetcher {

    private static final String WEATHER_API_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private static final Set<String> ALLOWED_STATIONS = new HashSet<>();

    static {
        ALLOWED_STATIONS.add("Tallinn-Harku");
        ALLOWED_STATIONS.add("Tartu-Tõravere");
        ALLOWED_STATIONS.add("Pärnu");
    }

    @Autowired
    private WeatherRepository weatherRepository;

    @Scheduled(cron = "0 15 * * * *")
    public void fetchWeatherData() {
        System.out.println("Fetching weather data...");

        try {
            URL url = new URL(WEATHER_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(connection.getInputStream());
            document.getDocumentElement().normalize();

            String timestampStr = document.getDocumentElement().getAttribute("timestamp");
            LocalDateTime timestamp = convertUnixTimestamp(timestampStr);

            NodeList stations = document.getElementsByTagName("station");

            for (int i = 0; i < stations.getLength(); i++) {
                Node node = stations.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String stationName = getElementValue(element, "name");
                    String airTemperature = getElementValue(element, "airtemperature");
                    String windSpeed = getElementValue(element, "windspeed");
                    String weatherPhenomenon = getElementValue(element, "phenomenon");
                    String wmoCode = getElementValue(element, "wmocode");


                    if (stationName != null && ALLOWED_STATIONS.contains(stationName)) {
                        WeatherData weatherData = new WeatherData();
                        weatherData.setStationName(stationName);
                        if (airTemperature != null)
                            weatherData.setAirTemperature(Double.parseDouble(airTemperature));
                        if (windSpeed != null)
                            weatherData.setWindSpeed(Double.parseDouble(windSpeed));
                        weatherData.setWeatherPhenomenon(weatherPhenomenon);
                        if (wmoCode != null)
                            weatherData.setWmoCode(Integer.parseInt(wmoCode));
                        weatherData.setTimestamp(timestamp);

                        weatherRepository.save(weatherData);
                    }
                }
            }

            System.out.println("Weather data fetched successfully!");
        } catch (Exception e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
        }
    }

    private String getElementValue(Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list.getLength() == 0) {
            return null;
        }

        String textContent = list.item(0).getTextContent();

        if (textContent == null || textContent.isEmpty())
            return null;

        return textContent;
    }

    private LocalDateTime convertUnixTimestamp(String unixTimestamp) {
        try {
            long epochSeconds = Long.parseLong(unixTimestamp);
            return Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (NumberFormatException e) {
            System.err.println("Invalid timestamp format: " + unixTimestamp);
            return LocalDateTime.now();
        }
    }
}