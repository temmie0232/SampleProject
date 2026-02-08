package com.example.workflow.application;

import java.time.LocalDateTime;

public record ApplicationDetailResponse(
        Long id,
        Long applicantUserId,
        String currentAddress,
        String newAddress,
        String reason,
        ApplicationStatus status,
        LocalDateTime submittedAt,
        LocalDateTime decidedAt,
        Long decidedByUserId,
        String rejectionReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
