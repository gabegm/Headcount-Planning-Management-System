package com.gaucimaistre.headcount.config;

import com.gaucimaistre.headcount.security.AppUserDetails;
import com.gaucimaistre.headcount.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    private final AuditService auditService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return;
        }
        int status = response.getStatus();
        if (status >= 500) {
            return;
        }
        try {
            Integer userId = extractUserId();
            String uri = request.getRequestURI();
            String action = extractAction(uri);
            String table = extractTable(uri);
            String function = request.getMethod() + " " + uri;
            Integer rowId = extractRowId(uri);
            auditService.log(request, action, table, function, rowId, userId);
        } catch (Exception e) {
            log.warn("Audit log failed for {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        }
    }

    private Integer extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails details) {
            return details.getUserId();
        }
        return null;
    }

    private String extractAction(String uri) {
        String lower = uri.toLowerCase();
        if (lower.contains("/login"))    return "LOGIN";
        if (lower.contains("/logout"))   return "LOGOUT";
        if (lower.contains("/register")) return "REGISTER";
        if (lower.contains("/delete"))   return "DELETE";
        if (lower.contains("/update") || lower.contains("/approve") || lower.contains("/reject")) return "UPDATE";
        if (lower.contains("/upload"))   return "UPLOAD";
        return "CREATE";
    }

    private String extractTable(String uri) {
        String[] parts = uri.replaceFirst("^/", "").split("/");
        for (String part : parts) {
            if (part.isBlank()) continue;
            switch (part.toLowerCase()) {
                case "admin", "api", "auth", "create", "update", "delete",
                     "upload", "approve", "reject", "login", "logout",
                     "register", "edit-form" -> { continue; }
                default -> {
                    if (part.matches("\\d+")) continue;
                    String table = part.toLowerCase();
                    if (table.endsWith("ies")) table = table.substring(0, table.length() - 3) + "y";
                    else if (table.endsWith("ses") || table.endsWith("xes") || table.endsWith("zes")) {
                        table = table.substring(0, table.length() - 2);
                    } else if (table.endsWith("s")) {
                        table = table.substring(0, table.length() - 1);
                    }
                    return table;
                }
            }
        }
        return "unknown";
    }

    private Integer extractRowId(String uri) {
        String[] parts = uri.split("/");
        for (String part : parts) {
            if (part.matches("\\d+")) {
                try { return Integer.parseInt(part); } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }
}
