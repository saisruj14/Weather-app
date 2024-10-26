package com.spring.weatherapplication.model;

import java.io.Serializable;
import java.time.LocalDate;

public class WeatherSummaryId implements Serializable {
    private LocalDate date;
    private String city;

    // Default constructor
    public WeatherSummaryId() {
    }

    public WeatherSummaryId(LocalDate date, String city) {
        this.date = date;
        this.city = city;
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

    // Override equals and hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeatherSummaryId)) return false;
        WeatherSummaryId that = (WeatherSummaryId) o;
        return date.equals(that.date) && city.equals(that.city);
    }

    @Override
    public int hashCode() {
        return 31 * date.hashCode() + city.hashCode();
    }
}
