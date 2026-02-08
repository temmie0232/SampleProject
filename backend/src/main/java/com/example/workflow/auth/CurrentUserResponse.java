package com.example.workflow.auth;

import com.example.workflow.user.Role;

public record CurrentUserResponse(
        Long userId,
        String loginId,
        String displayName,
        Role role
) {
}
