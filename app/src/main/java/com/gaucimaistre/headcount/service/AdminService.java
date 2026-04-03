package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserService userService;
    private final SubmissionService submissionService;
    private final AuditService auditService;

    public Map<String, Object> getDashboardData() {
        return Map.of(
                "userCount", userService.findAll().size(),
                "submissionCount", submissionService.findAll().size()
        );
    }

    public List<AuditLog> getAuditLogs() {
        return auditService.findAll();
    }
}
