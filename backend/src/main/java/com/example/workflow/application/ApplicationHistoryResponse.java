package com.example.workflow.application;

import java.time.LocalDateTime;

public record ApplicationHistoryResponse(
        Long id,
        ApplicationStatus fromStatus,
        ApplicationStatus toStatus,
        Long actedByUserId,
        String comment,
        LocalDateTime actedAt
) {
}
