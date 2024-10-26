package com.spring.weatherapplication.repository;

import com.spring.weatherapplication.model.WeatherSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeatherSummaryRepository extends JpaRepository<WeatherSummary, Long> {

    // Method to find the top 5 weather summaries for today
    @Query("SELECT ws FROM WeatherSummary ws WHERE DATE(ws.date) = :today ORDER BY ws.maxTemp DESC")
    Page<WeatherSummary> findTop6ByDate(@Param("today") LocalDate today, Pageable pageable);

    // Method to fetch recent weather summaries within a date range
    @Query("SELECT ws FROM WeatherSummary ws WHERE ws.date BETWEEN :startDate AND :endDate ORDER BY ws.date ASC")
    List<WeatherSummary> findRecentMaxTempsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



}
