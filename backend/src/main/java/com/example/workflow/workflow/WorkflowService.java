package com.example.workflow.workflow;

import com.example.workflow.application.ApplicationStatus;
import com.example.workflow.common.BusinessException;
import com.example.workflow.common.ErrorCode;
import com.example.workflow.user.Role;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final WorkflowTransitionRuleRepository ruleRepository;

    public WorkflowService(WorkflowTransitionRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public void validateTransition(ApplicationStatus from, ApplicationStatus to, Role actorRole) {
        boolean allowed = ruleRepository.existsByFromStatusAndToStatusAndActorRoleAndActiveIsTrue(from, to, actorRole);
        if (!allowed) {
            throw new BusinessException(ErrorCode.APPLICATION_INVALID_TRANSITION);
        }
    }
}
