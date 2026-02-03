package com.example.simplelibrary.auth;

import com.example.simplelibrary.AbstractIntegrationTest;
import com.example.simplelibrary.auth.dto.AuthResponse;
import com.example.simplelibrary.auth.dto.LoginRequest;
import com.example.simplelibrary.auth.dto.SignupRequest;
import com.example.simplelibrary.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void signupAndLoginWorks() {
        SignupRequest signup = new SignupRequest();
        signup.setEmail("user@example.com");
        signup.setPassword("Passw0rd!");
        signup.setDisplayName("User");

        authService.signup(signup);

        assertThat(userRepository.existsByEmailIgnoreCase("user@example.com")).isTrue();

        LoginRequest login = new LoginRequest();
        login.setEmail("user@example.com");
        login.setPassword("Passw0rd!");

        AuthResponse response = authService.login(login);
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isGreaterThan(0);
    }
}

