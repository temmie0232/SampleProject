package com.example.workflow.workflow;

import com.example.workflow.application.ApplicationStatus;
import com.example.workflow.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowTransitionRuleRepository extends JpaRepository<WorkflowTransitionRuleEntity, Long> {
    boolean existsByFromStatusAndToStatusAndActorRoleAndActiveIsTrue(
            ApplicationStatus fromStatus,
            ApplicationStatus toStatus,
            Role actorRole
    );
}
