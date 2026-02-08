package com.example.workflow.config;

import com.example.workflow.common.ApiErrorResponse;
import com.example.workflow.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(ErrorCode.AUTH_FORBIDDEN.getStatus().value());
        response.setContentType("application/json");

        ApiErrorResponse body = new ApiErrorResponse(
                getRequestId(request),
                ErrorCode.AUTH_FORBIDDEN.getCode(),
                ErrorCode.AUTH_FORBIDDEN.getDefaultMessage(),
                List.of()
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private String getRequestId(HttpServletRequest request) {
        Object value = request.getAttribute("requestId");
        return value == null ? "unknown" : value.toString();
    }
}
