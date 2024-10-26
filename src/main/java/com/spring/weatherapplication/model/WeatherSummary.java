package com.spring.weatherapplication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_summary")
@IdClass(WeatherSummaryId.class) // Composite key class
public class WeatherSummary {

    @Id
    private LocalDate date; // Using LocalDate as part of the primary key

    @Id
    private String city; // City as part of the primary key

    private double avgTemp; // Average temperature in Celsius
    private double maxTemp; // Maximum temperature in Celsius
    private double minTemp; // Minimum temperature in Celsius
    private String dominantCondition; // Dominant weather condition (e.g., Clear, Rain)
    private double humidity; // Humidity percentage
    private double windSpeed; // Wind speed in meters per second
    private double pressure; // Atmospheric pressure in hPa
    private double visibility; // Visibility in meters
    private LocalDateTime updatedTime; // Last updated time of the summary
    private boolean alertTriggered; // Field for tracking alert status

    // Parameterized constructor
    public WeatherSummary(LocalDate date, String city, double avgTemp, double maxTemp, double minTemp,
                          String dominantCondition, double humidity, double windSpeed,
                          double pressure, double visibility, LocalDateTime updatedTime,
                          boolean alertTriggered) {
        this.date = date;
        this.city = city;
        this.avgTemp = avgTemp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.dominantCondition = dominantCondition;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.visibility = visibility;
        this.updatedTime = updatedTime;
        this.alertTriggered = alertTriggered;
    }

    public WeatherSummary() {
    }

    // Getters and setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getAvgTemp() {
        return avgTemp;
    }

    public void setAvgTemp(double avgTemp) {
        this.avgTemp = avgTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public String getDominantCondition() {
        return dominantCondition;
    }

    public void setDominantCondition(String dominantCondition) {
        this.dominantCondition = dominantCondition;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isAlertTriggered() {
        return alertTriggered;
    }

    public void setAlertTriggered(boolean alertTriggered) {
        this.alertTriggered = alertTriggered;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "WeatherSummary{" +
                "date=" + date +
                ", city='" + city + '\'' +
                ", avgTemp=" + avgTemp +
                ", maxTemp=" + maxTemp +
                ", minTemp=" + minTemp +
                ", dominantCondition='" + dominantCondition + '\'' +
                ", humidity=" + humidity +
                ", windSpeed=" + windSpeed +
                ", pressure=" + pressure +
                ", visibility=" + visibility +
                ", updatedTime=" + updatedTime +
                ", alertTriggered=" + alertTriggered +
                '}';
    }
}
