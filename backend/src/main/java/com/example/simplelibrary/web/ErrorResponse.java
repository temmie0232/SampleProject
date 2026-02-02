package com.example.simplelibrary.web;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    private Instant timestamp;
    private String path;
    private ErrorBody error;

    public ErrorResponse(Instant timestamp, String path, ErrorBody error) {
        this.timestamp = timestamp;
        this.path = path;
        this.error = error;
    }

    public static ErrorResponse of(Instant timestamp, String path, String code, String message) {
        return new ErrorResponse(timestamp, path, new ErrorBody(code, message, List.of()));
    }

    public static ErrorResponse of(Instant timestamp, String path, String code, String message, List<ErrorDetail> details) {
        return new ErrorResponse(timestamp, path, new ErrorBody(code, message, details));
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public ErrorBody getError() {
        return error;
    }

    public static class ErrorBody {
        private String code;
        private String message;
        private List<ErrorDetail> details;

        public ErrorBody(String code, String message, List<ErrorDetail> details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public List<ErrorDetail> getDetails() {
            return details;
        }
    }
}

