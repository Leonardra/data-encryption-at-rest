package com.github.leonardra.data_encryption_at_rest.exceptions;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class SecurityErrorHandler {

    public void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        String json = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"Unauthorized\", \"message\": \"%s\"}",
                LocalDateTime.now(), status, message
        );
        response.getWriter().write(json);
    }
}
