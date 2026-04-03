package com.gaucimaistre.headcount.model;

import java.time.LocalDate;

public record Gatekeeping(
        int id,
        LocalDate date,
        LocalDate submissionDeadline,
        String notes
) {}
