package com.example.workflow.common;

import java.util.List;

public record ApiErrorResponse(
        String requestId,
        String code,
        String message,
        List<String> details
) {
}
