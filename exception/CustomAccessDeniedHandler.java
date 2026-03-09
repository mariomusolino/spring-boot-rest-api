package com.odissey.tour.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#403",
                HttpStatus.FORBIDDEN,
                request.getRequestURI(),
                "Non hai i permessi per accedere a questa risorsa."
        );
        String jsonError = objectMapper.writeValueAsString(customErrorResponse);
        response.getWriter().write(jsonError);

    }
}
