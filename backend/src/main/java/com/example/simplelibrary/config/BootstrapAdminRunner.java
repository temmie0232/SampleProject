package com.example.simplelibrary.config;

import com.example.simplelibrary.user.Role;
import com.example.simplelibrary.user.User;
import com.example.simplelibrary.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BootstrapAdminRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminRunner.class);
    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapAdminRunner(AppProperties appProperties, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.appProperties = appProperties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!appProperties.getBootstrap().isEnabled()) {
            return;
        }
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);
        if (adminExists) {
            return;
        }
        String email = appProperties.getBootstrap().getAdminEmail();
        String password = appProperties.getBootstrap().getAdminPassword();
        String displayName = appProperties.getBootstrap().getAdminDisplayName();
        if (email == null || password == null || displayName == null) {
            log.warn("Bootstrap admin is enabled but missing configuration");
            return;
        }
        User admin = new User();
        admin.setEmail(email.toLowerCase());
        admin.setDisplayName(displayName);
        admin.setRole(Role.ADMIN);
        admin.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(admin);
        log.info("Bootstrap admin created: {}", email);
    }
}
