package com.example.workflow.auth;

import com.example.workflow.common.ApiResponse;
import com.example.workflow.common.RequestIdHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RequestIdHolder requestIdHolder;

    public AuthController(AuthService authService, RequestIdHolder requestIdHolder) {
        this.authService = authService;
        this.requestIdHolder = requestIdHolder;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.of(requestIdHolder.getRequestId(), authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me() {
        return ApiResponse.of(requestIdHolder.getRequestId(), authService.me());
    }
}
