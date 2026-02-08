package com.example.workflow.application;

import com.example.workflow.auth.AuthContextService;
import com.example.workflow.auth.CustomUserDetails;
import com.example.workflow.common.ApiResponse;
import com.example.workflow.common.PageResponse;
import com.example.workflow.common.RequestIdHolder;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final AuthContextService authContextService;
    private final RequestIdHolder requestIdHolder;

    public ApplicationController(ApplicationService applicationService,
                                 AuthContextService authContextService,
                                 RequestIdHolder requestIdHolder) {
        this.applicationService = applicationService;
        this.authContextService = authContextService;
        this.requestIdHolder = requestIdHolder;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<PageResponse<ApplicationSummaryResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        CustomUserDetails user = authContextService.getCurrentUser();
        return ApiResponse.of(requestIdHolder.getRequestId(),
                applicationService.search(q, status, page, size, sort, user));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ApplicationDetailResponse> create(@Valid @RequestBody ApplicationCreateRequest request) {
        CustomUserDetails user = authContextService.getCurrentUser();
        return ApiResponse.of(requestIdHolder.getRequestId(), applicationService.create(request, user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<ApplicationDetailResponse> detail(@PathVariable Long id) {
        CustomUserDetails user = authContextService.getCurrentUser();
        return ApiResponse.of(requestIdHolder.getRequestId(), applicationService.findById(id, user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ApplicationDetailResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody ApplicationUpdateRequest request) {
        CustomUserDetails user = authContextService.getCurrentUser();
        return ApiResponse.of(requestIdHolder.getRequestId(), applicationService.update(id, request, user));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ApplicationDetailResponse> submit(@PathVariable Long id,
                                                         @Valid @RequestBody(required = false) ApplicationActionRequest request) {
        CustomUserDetails user = authContextService.getCurrentUser();
        ApplicationActionRequest actualRequest = request == null ? new ApplicationActionRequest(null, null) : request;
        return ApiResponse.of(requestIdHolder.getRequestId(), applicationService.submit(id, actualRequest, user));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ApplicationDetailResponse> approve(@PathVariable Long id,
                                                          @Valid @RequestBody(required = false) ApplicationActionRequest request) {
        CustomUserDetails user = authContextService.getCurrentUser();
        ApplicationActionRequest actualRequest = request == null ? new ApplicationActionRequest(null, null) : request;
        return ApiResponse.of(requestIdHolder.getRequestId(), applicationService.approve(id, actualRequest, user));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ApplicationDetailResponse> reject(@PathVariable Long id,
                                                         @Valid @RequestBody RejectApplicationRequest request) {
        CustomUserDetails user = authContextService.getCurrentUser();
        return ApiResponse.of(requestIdHolder.getRequestId(), applicationService.reject(id, request, user));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<List<ApplicationHistoryResponse>> history(@PathVariable Long id) {
        CustomUserDetails user = authContextService.getCurrentUser();
        return ApiResponse.of(requestIdHolder.getRequestId(), applicationService.history(id, user));
    }
}
