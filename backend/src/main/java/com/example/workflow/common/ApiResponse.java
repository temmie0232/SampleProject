package com.example.workflow.common;

public record ApiResponse<T>(
        String requestId,
        T data
) {
    public static <T> ApiResponse<T> of(String requestId, T data) {
        return new ApiResponse<>(requestId, data);
    }
}
