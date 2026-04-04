package com.gaucimaistre.headcount.model;

import java.time.OffsetDateTime;

public record AuditLog(
        int id,
        Integer userId,
        OffsetDateTime date,
        String userAgent,
        String ipAddress,
        String domain,
        String tbl,
        Integer rowId,
        String function,
        String action
) {}
