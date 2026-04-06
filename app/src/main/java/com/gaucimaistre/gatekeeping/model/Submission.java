package com.gaucimaistre.gatekeeping.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Submission(
        int id,
        int submitterId,
        int gatekeepingId,
        String positionId,
        int statusId,
        int reasonId,
        String rationale,
        LocalDate effectiveDate,
        OffsetDateTime submitted,
        String comment
) {}
