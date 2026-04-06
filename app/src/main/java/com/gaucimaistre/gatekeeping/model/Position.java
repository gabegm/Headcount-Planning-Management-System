package com.gaucimaistre.gatekeeping.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Position(
        int id,
        int statusId,
        int recruitmentStatusId,
        String number,
        int pillarId,
        int companyId,
        int departmentId,
        int functionId,
        boolean isBudget,
        String title,
        String functionalReportingLine,
        String disciplinaryReportingLine,
        String holder,
        Integer hours,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal salary,
        BigDecimal fringeBenefit,
        BigDecimal socialSecurityContribution,
        BigDecimal performanceBonus,
        BigDecimal superBonus,
        BigDecimal managementBonus
) {}
