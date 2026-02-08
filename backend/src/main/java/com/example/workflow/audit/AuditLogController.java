package com.example.workflow.audit;

import com.example.workflow.common.ApiResponse;
import com.example.workflow.common.PageResponse;
import com.example.workflow.common.RequestIdHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final RequestIdHolder requestIdHolder;

    public AuditLogController(AuditLogService auditLogService, RequestIdHolder requestIdHolder) {
        this.auditLogService = auditLogService;
        this.requestIdHolder = requestIdHolder;
    }

    @GetMapping
    public ApiResponse<PageResponse<AuditLogResponse>> search(
            @RequestParam(required = false) Long actorUserId,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.of(requestIdHolder.getRequestId(),
                auditLogService.search(actorUserId, actionType, from, to, page, size));
    }
}
