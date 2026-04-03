package com.gaucimaistre.headcount.model;

import java.time.OffsetDateTime;

public record Page(
        int id,
        OffsetDateTime submitted,
        OffsetDateTime edited,
        String name,
        String title,
        String body
) {}
