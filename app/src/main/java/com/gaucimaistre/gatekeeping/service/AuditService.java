package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.AuditLog;
import com.gaucimaistre.gatekeeping.repository.AuditRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditRepository auditRepository;

    public void log(HttpServletRequest request, String action, String table, String function,
                    Integer rowId, Integer userId) {
        try {
            String userAgent = request.getHeader("User-Agent");
            String forwarded = request.getHeader("X-Forwarded-For");
            String ipAddress = (forwarded != null && !forwarded.isBlank())
                    ? forwarded.split(",")[0].trim()
                    : request.getRemoteAddr();
            String hostHeader = request.getHeader("Host");
            String domain = (hostHeader != null && !hostHeader.isBlank())
                    ? hostHeader
                    : request.getServerName();

            AuditLog auditLog = new AuditLog(0, userId, null, userAgent, ipAddress, domain,
                    table, rowId, function, action);
            auditRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: action={}, table={}, rowId={}", action, table, rowId, e);
        }
    }

    public List<AuditLog> findAll() {
        return auditRepository.findAll();
    }

    public List<AuditLog> findByUserId(int userId) {
        return auditRepository.findByUserId(userId);
    }
}
