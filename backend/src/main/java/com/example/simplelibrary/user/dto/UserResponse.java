package com.example.simplelibrary.user.dto;

import com.example.simplelibrary.user.Role;

public class UserResponse {
    private String id;
    private String email;
    private String displayName;
    private Role role;

    public UserResponse(String id, String email, String displayName, Role role) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Role getRole() {
        return role;
    }
}

