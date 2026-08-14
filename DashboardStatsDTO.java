package com.honeypot.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    private long totalAttacks;
    private long failedLogins;
    private long successfulLogins;
    private long uniqueAttackerIps;
    private String threatLevel;
    private String primaryAttackType;
    private double riskScore;
    private String recommendation;
    private List<Map<String, Object>> timeline;
    private List<Map<String, Object>> recentLogs;
    private List<Map<String, Object>> topCredentials;
}
