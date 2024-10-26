package com.spring.weatherapplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.weatherapplication.model.WeatherSummary;
import com.spring.weatherapplication.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    // Jackson ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Handles the request to view the weather dashboard.
     *
     * @param model Model to carry data to the view.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("Accessing dashboard...");

        try {
            // Fetch daily weather summaries from the service
            List<WeatherSummary> summaries = weatherService.getDailySummaries();
            System.out.println("Weather Summaries: " + summaries);

            if (summaries.isEmpty()) {
                System.out.println("No weather data available.");
                model.addAttribute("message", "No weather data available.");
            } else {
                // Extracting unique cities and date labels
                List<String> cities = summaries.stream()
                        .map(WeatherSummary::getCity)
                        .distinct()
                        .collect(Collectors.toList());

                List<String> labels = summaries.stream()
                        .map(summary -> summary.getDate().format(DATE_FORMATTER)) // Format LocalDate
                        .distinct()
                        .collect(Collectors.toList());

                // Collecting max temperatures
                List<Double> maxTemps = summaries.stream()
                        .map(WeatherSummary::getMaxTemp)
                        .collect(Collectors.toList());

                System.out.println("Cities: " + cities);
                System.out.println("Labels: " + labels);
                System.out.println("Max Temperatures: " + maxTemps);

                // Check if any alert has been triggered
                boolean alertTriggered = summaries.stream()
                        .anyMatch(WeatherSummary::isAlertTriggered);

                // Fetch recent max temperatures for the last 5 days for each city
                Map<String, List<Double>> recentMaxTemps = weatherService.getRecentMaxTemperatures();

                // Serialize recentMaxTemps to JSON string
                String recentMaxTempsJson;
                try {
                    recentMaxTempsJson = objectMapper.writeValueAsString(recentMaxTemps);
                } catch (JsonProcessingException e) {
                    System.out.println("Error serializing recentMaxTemps: " + e.getMessage());
                    recentMaxTempsJson = "{}"; // Fallback to empty JSON
                }

                // Add attributes to the model
                model.addAttribute("summaries", summaries);
                model.addAttribute("cities", cities);
                model.addAttribute("labels", labels);
                model.addAttribute("maxTemps", maxTemps);
                model.addAttribute("alertTriggered", alertTriggered);
                model.addAttribute("recentMaxTemps", recentMaxTempsJson); // Pass the JSON string
            }
        } catch (Exception e) {
            System.out.println("Error accessing dashboard: " + e.getMessage());
            model.addAttribute("error", "Unable to retrieve weather data at the moment.");
        }

        return "dashboard"; // Return the dashboard view
    }

    @PostMapping("/add-summary") // Endpoint to add weather summaries
    public ResponseEntity<String> addWeatherSummary() {
        try {
            weatherService.addWeatherSummary();
            return ResponseEntity.ok("Weather summaries added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding weather summaries: " + e.getMessage());
        }
    }
}
