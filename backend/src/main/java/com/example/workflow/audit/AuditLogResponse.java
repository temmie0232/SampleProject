package com.example.workflow.audit;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String requestId,
        Long actorUserId,
        String actionType,
        String targetType,
        String targetId,
        AuditResult result,
        String detailsJson,
        LocalDateTime createdAt
) {
}
