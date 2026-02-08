package com.example.workflow.application.service;

import com.example.workflow.application.ApplicationCreateRequest;
import com.example.workflow.application.ApplicationDetailResponse;
import com.example.workflow.application.ApplicationEntity;
import com.example.workflow.application.ApplicationRepository;
import com.example.workflow.application.ApplicationService;
import com.example.workflow.application.ApplicationStatus;
import com.example.workflow.application.ApplicationStatusHistoryRepository;
import com.example.workflow.auth.CustomUserDetails;
import com.example.workflow.common.BusinessException;
import com.example.workflow.user.Role;
import com.example.workflow.user.UserEntity;
import com.example.workflow.workflow.WorkflowService;
import com.example.workflow.audit.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationStatusHistoryRepository historyRepository;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ApplicationService applicationService;

    private CustomUserDetails user;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setLoginId("user01");
        userEntity.setPasswordHash("{noop}user123");
        userEntity.setRole(Role.USER);
        userEntity.setDisplayName("一般 花子");
        userEntity.setEnabled(true);
        user = new CustomUserDetails(userEntity);
    }

    @Test
    void create_正常系_下書きで作成される() {
        when(applicationRepository.save(any())).thenAnswer(invocation -> {
            ApplicationEntity entity = invocation.getArgument(0);
            // save後の最小限フィールドのみ模擬
            return entity;
        });

        ApplicationDetailResponse response = applicationService.create(new ApplicationCreateRequest(
                "東京都千代田区1-1",
                "東京都千代田区2-2",
                "転居のため"
        ), user);

        assertThat(response.status()).isEqualTo(ApplicationStatus.DRAFT);
        assertThat(response.newAddress()).isEqualTo("東京都千代田区2-2");
    }

    @Test
    void findById_異常系_他人申請は参照不可() {
        ApplicationEntity entity = new ApplicationEntity();
        entity.setApplicantUserId(999L);
        entity.setStatus(ApplicationStatus.DRAFT);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> applicationService.findById(1L, user))
                .isInstanceOf(BusinessException.class);
    }
}
