package com.example.workflow.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-001", "ログインIDまたはパスワードが不正です"),
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-002", "認証が必要です"),
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH-003", "権限がありません"),

    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APP-001", "申請が見つかりません"),
    APPLICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "APP-002", "この申請にアクセスできません"),
    APPLICATION_INVALID_TRANSITION(HttpStatus.BAD_REQUEST, "APP-003", "許可されていない状態遷移です"),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON-001", "入力値が不正です"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-999", "予期しないエラーが発生しました");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
