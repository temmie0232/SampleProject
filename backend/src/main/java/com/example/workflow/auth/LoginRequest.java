package com.example.workflow.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "ログインIDは必須です")
        String loginId,

        @NotBlank(message = "パスワードは必須です")
        String password
) {
}
