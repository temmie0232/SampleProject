package com.example.workflow.application;

import com.example.workflow.audit.AuditLogService;
import com.example.workflow.audit.AuditResult;
import com.example.workflow.auth.CustomUserDetails;
import com.example.workflow.common.BusinessException;
import com.example.workflow.common.ErrorCode;
import com.example.workflow.common.PageResponse;
import com.example.workflow.user.Role;
import com.example.workflow.workflow.WorkflowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationService {
    // 申請の業務ルール（権限・状態遷移・監査）をServiceに集約し、Controllerは入出力に専念させる。

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final WorkflowService workflowService;
    private final AuditLogService auditLogService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ApplicationStatusHistoryRepository historyRepository,
                              WorkflowService workflowService,
                              AuditLogService auditLogService) {
        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
        this.workflowService = workflowService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public ApplicationDetailResponse create(ApplicationCreateRequest request, CustomUserDetails user) {
        ApplicationEntity entity = new ApplicationEntity();
        entity.setApplicantUserId(user.getId());
        entity.setCurrentAddress(request.currentAddress());
        entity.setNewAddress(request.newAddress());
        entity.setReason(request.reason());
        entity.setStatus(ApplicationStatus.DRAFT);

        ApplicationEntity saved = applicationRepository.save(entity);
        auditLogService.record(user.getId(), "APPLICATION_CREATE", "APPLICATION", String.valueOf(saved.getId()), AuditResult.SUCCESS,
                Map.of("status", saved.getStatus().name()));

        return toDetail(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationSummaryResponse> search(String q,
                                                           ApplicationStatus status,
                                                           int page,
                                                           int size,
                                                           String sort,
                                                           CustomUserDetails user) {
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Long applicantUserId = user.getRole() == Role.ADMIN ? null : user.getId();
        String normalizedQ = (q == null || q.isBlank()) ? null : q;

        Page<ApplicationSummaryResponse> result = applicationRepository
                .search(applicantUserId, status, normalizedQ, pageable)
                .map(this::toSummary);

        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public ApplicationDetailResponse findById(Long id, CustomUserDetails user) {
        ApplicationEntity entity = getApplication(id);
        validateReadPermission(entity, user);
        return toDetail(entity);
    }

    @Transactional
    public ApplicationDetailResponse update(Long id, ApplicationUpdateRequest request, CustomUserDetails user) {
        ApplicationEntity entity = getApplication(id);
        validateOwner(entity, user);

        if (!(entity.getStatus() == ApplicationStatus.DRAFT || entity.getStatus() == ApplicationStatus.REJECTED)) {
            throw new BusinessException(ErrorCode.APPLICATION_INVALID_TRANSITION, "下書きまたは却下状態のみ更新できます");
        }

        entity.setCurrentAddress(request.currentAddress());
        entity.setNewAddress(request.newAddress());
        entity.setReason(request.reason());

        ApplicationEntity saved = applicationRepository.save(entity);
        auditLogService.record(user.getId(), "APPLICATION_UPDATE", "APPLICATION", String.valueOf(saved.getId()), AuditResult.SUCCESS,
                Map.of("status", saved.getStatus().name()));

        return toDetail(saved);
    }

    @Transactional
    public ApplicationDetailResponse submit(Long id, ApplicationActionRequest request, CustomUserDetails user) {
        ApplicationEntity entity = getApplication(id);
        validateOwner(entity, user);

        ApplicationStatus from = entity.getStatus();
        ApplicationStatus to = ApplicationStatus.SUBMITTED;

        workflowService.validateTransition(from, to, user.getRole());

        entity.setStatus(to);
        entity.setSubmittedAt(LocalDateTime.now());
        entity.setRejectionReason(null);

        appendHistory(entity.getId(), from, to, user.getId(), request.comment());
        ApplicationEntity saved = applicationRepository.save(entity);

        auditLogService.record(user.getId(), "APPLICATION_SUBMIT", "APPLICATION", String.valueOf(saved.getId()), AuditResult.SUCCESS,
                Map.of("from", from.name(), "to", to.name()));

        return toDetail(saved);
    }

    @Transactional
    public ApplicationDetailResponse approve(Long id, ApplicationActionRequest request, CustomUserDetails user) {
        ApplicationEntity entity = getApplication(id);
        ApplicationStatus from = entity.getStatus();
        ApplicationStatus to = ApplicationStatus.APPROVED;

        workflowService.validateTransition(from, to, user.getRole());

        entity.setStatus(to);
        entity.setDecidedAt(LocalDateTime.now());
        entity.setDecidedByUserId(user.getId());

        appendHistory(entity.getId(), from, to, user.getId(), request.comment());
        ApplicationEntity saved = applicationRepository.save(entity);

        auditLogService.record(user.getId(), "APPLICATION_APPROVE", "APPLICATION", String.valueOf(saved.getId()), AuditResult.SUCCESS,
                Map.of("from", from.name(), "to", to.name()));

        return toDetail(saved);
    }

    @Transactional
    public ApplicationDetailResponse reject(Long id, RejectApplicationRequest request, CustomUserDetails user) {
        ApplicationEntity entity = getApplication(id);
        ApplicationStatus from = entity.getStatus();
        ApplicationStatus to = ApplicationStatus.REJECTED;

        workflowService.validateTransition(from, to, user.getRole());

        entity.setStatus(to);
        entity.setDecidedAt(LocalDateTime.now());
        entity.setDecidedByUserId(user.getId());
        entity.setRejectionReason(request.rejectionReason());

        appendHistory(entity.getId(), from, to, user.getId(), request.comment());
        ApplicationEntity saved = applicationRepository.save(entity);

        auditLogService.record(user.getId(), "APPLICATION_REJECT", "APPLICATION", String.valueOf(saved.getId()), AuditResult.SUCCESS,
                Map.of("from", from.name(), "to", to.name(), "reason", request.rejectionReason()));

        return toDetail(saved);
    }

    @Transactional(readOnly = true)
    public List<ApplicationHistoryResponse> history(Long id, CustomUserDetails user) {
        ApplicationEntity entity = getApplication(id);
        validateReadPermission(entity, user);

        return historyRepository.findByApplicationIdOrderByActedAtAsc(id)
                .stream()
                .map(h -> new ApplicationHistoryResponse(
                        h.getId(),
                        h.getFromStatus(),
                        h.getToStatus(),
                        h.getActedByUserId(),
                        h.getComment(),
                        h.getActedAt()
                ))
                .toList();
    }

    private void appendHistory(Long applicationId,
                               ApplicationStatus from,
                               ApplicationStatus to,
                               Long actedByUserId,
                               String comment) {
        ApplicationStatusHistoryEntity history = new ApplicationStatusHistoryEntity();
        history.setApplicationId(applicationId);
        history.setFromStatus(from);
        history.setToStatus(to);
        history.setActedByUserId(actedByUserId);
        history.setComment(comment);
        history.setActedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    private ApplicationEntity getApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    private void validateReadPermission(ApplicationEntity entity, CustomUserDetails user) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (!entity.getApplicantUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.APPLICATION_ACCESS_DENIED);
        }
    }

    private void validateOwner(ApplicationEntity entity, CustomUserDetails user) {
        if (!entity.getApplicantUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.APPLICATION_ACCESS_DENIED);
        }
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String[] tokens = sort.split(",");
        String property = tokens[0];
        Sort.Direction direction = tokens.length > 1 && "asc".equalsIgnoreCase(tokens[1])
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, property);
    }

    private ApplicationSummaryResponse toSummary(ApplicationEntity entity) {
        return new ApplicationSummaryResponse(
                entity.getId(),
                entity.getApplicantUserId(),
                entity.getNewAddress(),
                entity.getStatus(),
                entity.getSubmittedAt(),
                entity.getUpdatedAt()
        );
    }

    private ApplicationDetailResponse toDetail(ApplicationEntity entity) {
        return new ApplicationDetailResponse(
                entity.getId(),
                entity.getApplicantUserId(),
                entity.getCurrentAddress(),
                entity.getNewAddress(),
                entity.getReason(),
                entity.getStatus(),
                entity.getSubmittedAt(),
                entity.getDecidedAt(),
                entity.getDecidedByUserId(),
                entity.getRejectionReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
