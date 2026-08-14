package com.honeypot.analytics.controller;

import com.honeypot.analytics.dto.DashboardStatsDTO;
import com.honeypot.analytics.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<Map<String, Object>>> getTimeline() {
        return ResponseEntity.ok(dashboardService.getAttackTimeline());
    }

    @GetMapping("/recent-logs")
    public ResponseEntity<List<Map<String, Object>>> getRecentLogs() {
        return ResponseEntity.ok(dashboardService.getRecentAttacksWithAI());
    }

    @GetMapping("/recent-attacks")
    public ResponseEntity<List<Map<String, Object>>> getRecentAttacks() {
        return ResponseEntity.ok(dashboardService.getRecentAttacksWithAI());
    }

    @GetMapping("/top-credentials")
    public ResponseEntity<List<Map<String, Object>>> getTopCredentials() {
        return ResponseEntity.ok(dashboardService.getTopCredentials());
    }

    @GetMapping("/system-status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        return ResponseEntity.ok(dashboardService.getSystemStatus());
    }
}
