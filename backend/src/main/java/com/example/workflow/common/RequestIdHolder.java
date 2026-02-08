package com.example.workflow.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class RequestIdHolder {

    private final String requestId;

    public RequestIdHolder(HttpServletRequest request) {
        Object value = request.getAttribute("requestId");
        this.requestId = value == null ? "unknown" : value.toString();
    }

    public String getRequestId() {
        return requestId;
    }
}
