package com.example.workflow.application;

import java.time.LocalDateTime;

public record ApplicationSummaryResponse(
        Long id,
        Long applicantUserId,
        String newAddress,
        ApplicationStatus status,
        LocalDateTime submittedAt,
        LocalDateTime updatedAt
) {
}
