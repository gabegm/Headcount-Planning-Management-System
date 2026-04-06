package com.gaucimaistre.gatekeeping.service;

import java.util.Map;

public record DashboardData(
        Map<String, Double> budgetFte,
        Map<String, Double> actualFte,
        Map<String, Double> budgetCost,
        Map<String, Double> actualCost
) {}
