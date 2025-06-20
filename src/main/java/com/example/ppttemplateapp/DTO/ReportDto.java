package com.example.ppttemplateapp.DTO;

import java.util.Map;
import java.util.HashMap;
import com.example.ppttemplateapp.model.Report;

public record ReportDto(String title, String content) {

    public static ReportDto fromEntity(Report report) {
        return new ReportDto(report.getTitle(), report.getContent());
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        return map;
    }
}
    