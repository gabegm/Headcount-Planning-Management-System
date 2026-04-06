package com.gaucimaistre.gatekeeping.model;

import java.time.LocalDate;

public record Gatekeeping(
        int id,
        LocalDate date,
        LocalDate submissionDeadline,
        String notes
) {}
