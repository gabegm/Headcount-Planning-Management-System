package com.gaucimaistre.gatekeeping.model;

public record ExchangeRate(
        int id,
        String name,
        String code,
        java.math.BigDecimal rate
) {}
