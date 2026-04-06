package com.gaucimaistre.gatekeeping.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PositionView(
    int id,
    String statusName,
    String recruitmentStatusName,
    String number,
    String pillarName,
    String companyName,
    String departmentName,
    String functionName,
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
