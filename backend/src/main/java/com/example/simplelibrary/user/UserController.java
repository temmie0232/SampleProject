package com.example.simplelibrary.user;

import com.example.simplelibrary.security.SecurityUtils;
import com.example.simplelibrary.security.UserPrincipal;
import com.example.simplelibrary.user.dto.ChangePasswordRequest;
import com.example.simplelibrary.user.dto.UpdateProfileRequest;
import com.example.simplelibrary.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserResponse me() {
        UserPrincipal principal = SecurityUtils.currentUser();
        return userService.getById(principal.getId());
    }

    @PatchMapping
    public UserResponse update(@Valid @RequestBody UpdateProfileRequest request) {
        UserPrincipal principal = SecurityUtils.currentUser();
        return userService.updateProfile(principal.getId(), request);
    }

    @PostMapping("/password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UserPrincipal principal = SecurityUtils.currentUser();
        userService.changePassword(principal.getId(), request);
    }
}

