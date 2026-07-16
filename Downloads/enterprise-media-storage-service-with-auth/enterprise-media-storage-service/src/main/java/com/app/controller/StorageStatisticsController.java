package com.app.controller;

import com.app.dto.StorageStatisticsResponse;
import com.app.service.StorageStatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StorageStatisticsController {

    private final StorageStatisticsService storageStatisticsService;

    public StorageStatisticsController(StorageStatisticsService storageStatisticsService) {
        this.storageStatisticsService = storageStatisticsService;
    }

    @GetMapping
    public StorageStatisticsResponse getStatistics() {
        return storageStatisticsService.getStatistics();
    }
}
