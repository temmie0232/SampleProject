package com.example.workflow.auth;

import com.example.workflow.user.Role;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String loginId,
        String displayName,
        Role role
) {
}
