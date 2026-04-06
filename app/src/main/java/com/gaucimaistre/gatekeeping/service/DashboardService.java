package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Position;
import com.gaucimaistre.gatekeeping.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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

        if (functionId != null && functionId != 0) {
            positions = positions.stream()
                    .filter(p -> p.functionId() == functionId)
                    .toList();
        }

        // Sort by startDate so map keys are inserted in chronological order
        positions = positions.stream()
                .filter(p -> p.startDate() != null)
                .sorted(Comparator.comparing(Position::startDate))
                .toList();

        Map<String, Double> budgetFte = new LinkedHashMap<>();
        Map<String, Double> actualFte = new LinkedHashMap<>();
        Map<String, Double> budgetCost = new LinkedHashMap<>();
        Map<String, Double> actualCost = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-01");

        for (Position p : positions) {
            String monthKey = p.startDate().withDayOfMonth(1).format(formatter);
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
