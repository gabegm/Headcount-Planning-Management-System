package com.gaucimaistre.headcount.model;

import java.time.OffsetDateTime;

public record SubmissionChange(
        int id,
        int submissionId,
        OffsetDateTime submitted,
        String field,
        String change
) {}
