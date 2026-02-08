package com.example.workflow.auth.service;

import com.example.workflow.auth.AuthService;
import com.example.workflow.auth.JwtService;
import com.example.workflow.auth.LoginRequest;
import com.example.workflow.auth.LoginResponse;
import com.example.workflow.common.BusinessException;
import com.example.workflow.user.Role;
import com.example.workflow.user.UserEntity;
import com.example.workflow.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setLoginId("user01");
        user.setPasswordHash("{noop}user123");
        user.setRole(Role.USER);
        user.setDisplayName("一般 花子");
        user.setEnabled(true);
    }

    @Test
    void login_正常系_トークンを返す() {
        when(userRepository.findByLoginId("user01")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("user123", "{noop}user123")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("dummy-token");

        LoginResponse response = authService.login(new LoginRequest("user01", "user123"));

        assertThat(response.accessToken()).isEqualTo("dummy-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.loginId()).isEqualTo("user01");
        assertThat(response.role()).isEqualTo(Role.USER);
    }

    @Test
    void login_異常系_パスワード不一致() {
        when(userRepository.findByLoginId("user01")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("ng", "{noop}user123")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("user01", "ng")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ログインIDまたはパスワードが不正です");
    }
}
