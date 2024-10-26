package com.spring.weatherapplication.service;

import com.spring.weatherapplication.model.WeatherSummary;
import com.spring.weatherapplication.repository.WeatherSummaryRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private static final String API_KEY = "bbe012ad9d5957b26ba21fdbd3b8c695"; // Replace with your valid API key
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q={city}&appid=" + API_KEY;

    // Thresholds for alerting
    private static final double TEMP_THRESHOLD = 35.0; // Celsius
    private static final int CONSECUTIVE_THRESHOLD_COUNT = 2; // Consecutive times the threshold must be breached

    @Autowired
    private WeatherSummaryRepository repository;

    // Counter for consecutive threshold breaches (city-specific tracking)
    private int consecutiveAlertCount = 0;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void fetchWeatherData() {
        System.out.println("Fetching weather data...");
        String[] cities = {"Delhi", "Mumbai", "Chennai", "Bangalore", "Kolkata", "Hyderabad"};
        RestTemplate restTemplate = new RestTemplate();

        for (String city : cities) {
            try {
                // Fetch weather data
                String result = restTemplate.getForObject(API_URL, String.class, city);
                if (result != null) {
                    JSONObject json = new JSONObject(result);

                    // Parse temperature values
                    double tempCelsius = kelvinToCelsius(json.getJSONObject("main").getDouble("temp"));
                    double maxTempCelsius = kelvinToCelsius(json.getJSONObject("main").getDouble("temp_max"));
                    double minTempCelsius = kelvinToCelsius(json.getJSONObject("main").getDouble("temp_min"));

                    // Parse other weather data
                    String weatherCondition = json.getJSONArray("weather").getJSONObject(0).getString("main");
                    double humidity = json.getJSONObject("main").getDouble("humidity");
                    double windSpeed = json.getJSONObject("wind").getDouble("speed");
                    double pressure = json.getJSONObject("main").getDouble("pressure"); // Pressure
                    double visibility = json.getDouble("visibility"); // Visibility

                    // Trigger alert if temperature exceeds threshold
                    boolean alertTriggered = checkTemperatureThreshold(city, maxTempCelsius);

                    // Set the date to the current date
                    LocalDate date = LocalDate.now();

                    // Create WeatherSummary instance
                    if (city != null && !city.isEmpty()) {
                        WeatherSummary summary = new WeatherSummary(
                                date,
                                city,
                                roundToTwoDecimals(tempCelsius),
                                roundToTwoDecimals(maxTempCelsius),
                                roundToTwoDecimals(minTempCelsius),
                                weatherCondition,
                                humidity,
                                windSpeed,
                                pressure,
                                visibility,
                                LocalDateTime.now(),
                                alertTriggered
                        );

                        repository.save(summary); // Save the summary to the database
                        System.out.println("Saved weather summary for " + city + ": " + summary);
                    } else {
                        System.out.println("City is null or empty, skipping database insertion for city: " + city);
                    }
                } else {
                    System.out.println("No data returned for city: " + city);
                }
            } catch (Exception e) {
                System.out.println("Error fetching weather data for " + city + ": " + e.getMessage());
            }
        }
    }

    // Method to retrieve all weather summaries from the database
    public List<WeatherSummary> getDailySummaries() {
        LocalDate today = LocalDate.now(); // Get today's date
        Pageable pageable = PageRequest.of(0, 6); // Create a pageable request for 6 results
        return repository.findTop6ByDate(today, pageable).getContent(); // Retrieve top 6 summaries for today
    }

    // Check if max temperature exceeds the threshold and handle alerts
    private boolean checkTemperatureThreshold(String city, double maxTemp) {
        if (maxTemp > TEMP_THRESHOLD) {
            consecutiveAlertCount++;
            if (consecutiveAlertCount >= CONSECUTIVE_THRESHOLD_COUNT) {
                triggerAlert(city, maxTemp);
                return true;
            }
        } else {
            consecutiveAlertCount = 0; // Reset the count if threshold is not breached
        }
        return false;
    }

    // Trigger an alert (console-based alert for now, can be extended for email)
    private void triggerAlert(String city, double maxTemp) {
        System.out.println("ALERT: " + city + " has exceeded the maximum temperature threshold! Current Max Temp: " + maxTemp + "°C");
    }

    // Method to get recent maximum temperatures for the last 5 days for each city
    public Map<String, List<Double>> getRecentMaxTemperatures() {
        Map<String, List<Double>> recentMaxTemps = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(4); // This gives you a total of 5 days (including today)

        // Fetch recent weather summaries
        List<WeatherSummary> summaries = repository.findRecentMaxTempsByDateRange(startDate, today);

        // Group temperatures by city
        for (WeatherSummary summary : summaries) {
            recentMaxTemps
                    .computeIfAbsent(summary.getCity(), k -> new ArrayList<>())
                    .add(summary.getMaxTemp());
        }

        return recentMaxTemps; // Return the map of recent max temperatures
    }


    // Utility method to convert Kelvin to Celsius
    private double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }

    // Utility method to round double values to two decimal places
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Add weather summary method (to be called from the controller)
    public void addWeatherSummary() {
        // Creating and saving multiple WeatherSummary instances
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 19), "Bangalore", 27.0, 28.0, 26.0, "Clear", 50, 1.50, 1008, 10000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 19), "Chennai", 30.0, 32.0, 29.0, "Sunny", 60, 3.00, 1009, 5000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 19), "Delhi", 35.0, 36.0, 34.0, "Clear", 40, 2.50, 1007, 3000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 19), "Hyderabad", 28.0, 29.0, 27.0, "Partly Cloudy", 55, 2.10, 1008, 4000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 19), "Kolkata", 31.0, 32.0, 30.0, "Haze", 65, 3.00, 1007, 6000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 19), "Mumbai", 29.0, 31.0, 28.0, "Cloudy", 70, 3.50, 1006, 3000, LocalDateTime.now(), false));

        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 20), "Bangalore", 28.5, 29.5, 27.5, "Cloudy", 55, 1.70, 1008, 11000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 20), "Chennai", 31.0, 32.5, 30.5, "Sunny", 65, 3.10, 1009, 6000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 20), "Delhi", 34.5, 35.5, 33.5, "Haze", 45, 2.60, 1007, 4000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 20), "Hyderabad", 29.5, 30.5, 28.5, "Partly Cloudy", 50, 2.30, 1008, 4500, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 20), "Kolkata", 30.5, 31.5, 29.5, "Haze", 60, 3.80, 1007, 5500, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 20), "Mumbai", 30.0, 31.0, 29.0, "Rain", 75, 3.20, 1006, 4000, LocalDateTime.now(), false));

        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 21), "Bangalore", 27.5, 28.5, 26.5, "Clear", 50, 1.55, 1008, 10000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 21), "Chennai", 30.5, 32.0, 29.5, "Sunny", 70, 3.20, 1009, 5000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 21), "Delhi", 35.0, 36.0, 34.0, "Clear", 40, 2.75, 1007, 3000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 21), "Hyderabad", 29.0, 30.0, 28.0, "Partly Cloudy", 55, 2.10, 1008, 4000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 21), "Kolkata", 31.5, 32.5, 30.5, "Haze", 65, 4.10, 1007, 6000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 21), "Mumbai", 30.5, 31.5, 29.5, "Cloudy", 70, 3.40, 1006, 3000, LocalDateTime.now(), false));

        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 22), "Bangalore", 26.5, 27.5, 25.5, "Clear", 45, 1.40, 1008, 11000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 22), "Chennai", 31.0, 32.5, 29.5, "Sunny", 65, 3.30, 1009, 6000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 22), "Delhi", 34.0, 35.5, 33.5, "Clear", 35, 2.40, 1007, 4000, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 22), "Hyderabad", 28.5, 29.5, 27.5, "Partly Cloudy", 50, 2.20, 1008, 4500, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 22), "Kolkata", 30.0, 31.0, 29.0, "Haze", 60, 3.70, 1007, 5500, LocalDateTime.now(), false));
        repository.save(new WeatherSummary(LocalDate.of(2024, 10, 22), "Mumbai", 29.5, 30.5, 28.5, "Cloudy", 65, 3.10, 1006, 4000, LocalDateTime.now(), false));
    }

}
