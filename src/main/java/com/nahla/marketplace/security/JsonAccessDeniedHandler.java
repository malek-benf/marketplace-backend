package com.nahla.marketplace.security;

import com.nahla.marketplace.exception.JsonErrorWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        JsonErrorWriter.write(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                "You do not have permission to access this resource.", request.getRequestURI());
    }
}