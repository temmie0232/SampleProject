package com.example.workflow.workflow.service;

import com.example.workflow.application.ApplicationStatus;
import com.example.workflow.common.BusinessException;
import com.example.workflow.user.Role;
import com.example.workflow.workflow.WorkflowService;
import com.example.workflow.workflow.WorkflowTransitionRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private WorkflowTransitionRuleRepository repository;

    @InjectMocks
    private WorkflowService workflowService;

    @Test
    void validateTransition_正常系_許可される遷移() {
        when(repository.existsByFromStatusAndToStatusAndActorRoleAndActiveIsTrue(
                ApplicationStatus.DRAFT,
                ApplicationStatus.SUBMITTED,
                Role.USER
        )).thenReturn(true);

        workflowService.validateTransition(ApplicationStatus.DRAFT, ApplicationStatus.SUBMITTED, Role.USER);
    }

    @Test
    void validateTransition_異常系_許可されない遷移() {
        when(repository.existsByFromStatusAndToStatusAndActorRoleAndActiveIsTrue(
                ApplicationStatus.SUBMITTED,
                ApplicationStatus.DRAFT,
                Role.USER
        )).thenReturn(false);

        assertThatThrownBy(() -> workflowService.validateTransition(
                ApplicationStatus.SUBMITTED,
                ApplicationStatus.DRAFT,
                Role.USER
        )).isInstanceOf(BusinessException.class);
    }
}
