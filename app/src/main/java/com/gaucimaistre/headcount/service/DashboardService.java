package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Position;
import com.gaucimaistre.headcount.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final PositionService positionService;

    public DashboardData getChartData(int userId, UserType userType, Integer functionId) {
        List<Position> positions = positionService.findAll(userId, userType);

        if (functionId != null) {
            positions = positions.stream()
                    .filter(p -> p.functionId() == functionId)
                    .toList();
        }

        Map<String, Double> budgetFte = new LinkedHashMap<>();
        Map<String, Double> actualFte = new LinkedHashMap<>();
        Map<String, Double> budgetCost = new LinkedHashMap<>();
        Map<String, Double> actualCost = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (Position p : positions) {
            if (p.startDate() == null) {
                continue;
            }
            String monthKey = p.startDate().format(formatter);
            double fte = p.hours() != null ? p.hours() / 40.0 : 0.0;
            double cost = BigDecimal.ZERO
                    .add(p.salary() != null ? p.salary() : BigDecimal.ZERO)
                    .add(p.fringeBenefit() != null ? p.fringeBenefit() : BigDecimal.ZERO)
                    .add(p.socialSecurityContribution() != null ? p.socialSecurityContribution() : BigDecimal.ZERO)
                    .doubleValue();

            if (p.isBudget()) {
                budgetFte.merge(monthKey, fte, Double::sum);
                budgetCost.merge(monthKey, cost, Double::sum);
            } else {
                actualFte.merge(monthKey, fte, Double::sum);
                actualCost.merge(monthKey, cost, Double::sum);
            }
        }

        return new DashboardData(budgetFte, actualFte, budgetCost, actualCost);
    }

    @Deprecated
    public Map<String, Object> getChartData(int functionId) {
        throw new UnsupportedOperationException("Use getChartData(int userId, UserType userType, Integer functionId) instead");
    }
}
