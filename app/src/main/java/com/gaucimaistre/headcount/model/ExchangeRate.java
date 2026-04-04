package com.gaucimaistre.headcount.model;

public record ExchangeRate(
        int id,
        String name,
        String code,
        java.math.BigDecimal rate
) {}
