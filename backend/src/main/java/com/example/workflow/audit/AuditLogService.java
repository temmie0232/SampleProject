package com.example.workflow.audit;

import com.example.workflow.common.PageResponse;
import com.example.workflow.common.RequestIdHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final RequestIdHolder requestIdHolder;
    private final ObjectMapper objectMapper;

    public AuditLogService(AuditLogRepository auditLogRepository, RequestIdHolder requestIdHolder, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.requestIdHolder = requestIdHolder;
        this.objectMapper = objectMapper;
    }

    public void record(Long actorUserId, String actionType, String targetType, String targetId, AuditResult result, Map<String, Object> details) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setRequestId(requestIdHolder.getRequestId());
        entity.setActorUserId(actorUserId);
        entity.setActionType(actionType);
        entity.setTargetType(targetType);
        entity.setTargetId(targetId);
        entity.setResult(result);
        entity.setDetailsJson(writeJson(details));
        auditLogRepository.save(entity);
    }

    public PageResponse<AuditLogResponse> search(Long actorUserId,
                                                 String actionType,
                                                 LocalDateTime from,
                                                 LocalDateTime to,
                                                 int page,
                                                 int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<AuditLogEntity> spec = Specification.where(null);
        if (actorUserId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("actorUserId"), actorUserId));
        }
        if (actionType != null && !actionType.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("actionType"), actionType));
        }
        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to));
        }

        Page<AuditLogResponse> resultPage = auditLogRepository.findAll(spec, pageable)
                .map(entity -> new AuditLogResponse(
                        entity.getId(),
                        entity.getRequestId(),
                        entity.getActorUserId(),
                        entity.getActionType(),
                        entity.getTargetType(),
                        entity.getTargetId(),
                        entity.getResult(),
                        entity.getDetailsJson(),
                        entity.getCreatedAt()
                ));

        return PageResponse.from(resultPage);
    }

    private String writeJson(Map<String, Object> details) {
        try {
            return objectMapper.writeValueAsString(details == null ? Map.of() : details);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
