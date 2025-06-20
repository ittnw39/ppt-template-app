package com.example.ppttemplateapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ppttemplateapp.model.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

}
