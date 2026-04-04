package com.gaucimaistre.headcount.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record SubmissionView(
        int id,
        int submitterId,
        String submitterEmail,
        int gatekeepingId,
        String positionId,
        int statusId,
        String statusName,
        int reasonId,
        String reasonName,
        String rationale,
        LocalDate effectiveDate,
        OffsetDateTime submitted,
        String comment
) {}
