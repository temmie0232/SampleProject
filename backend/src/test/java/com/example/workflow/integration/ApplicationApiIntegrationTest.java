package com.example.workflow.integration;

import com.example.workflow.user.Role;
import com.example.workflow.user.UserEntity;
import com.example.workflow.user.UserRepository;
import com.example.workflow.workflow.WorkflowTransitionRuleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkflowTransitionRuleRepository ruleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupData() {
        ruleRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setLoginId("user01");
        user.setPasswordHash(passwordEncoder.encode("user123"));
        user.setRole(Role.USER);
        user.setDisplayName("一般 花子");
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Test
    void login_and_create_application() throws Exception {
        String loginBody = """
                {
                  "loginId": "user01",
                  "password": "user123"
                }
                """;

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(loginResponse);
        String token = root.path("data").path("accessToken").asText();

        String createBody = """
                {
                  "currentAddress": "東京都港区1-1",
                  "newAddress": "東京都港区2-2",
                  "reason": "転居"
                }
                """;

        mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }
}
