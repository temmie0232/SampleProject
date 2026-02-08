package com.example.workflow.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistoryEntity, Long> {
    List<ApplicationStatusHistoryEntity> findByApplicationIdOrderByActedAtAsc(Long applicationId);
}
