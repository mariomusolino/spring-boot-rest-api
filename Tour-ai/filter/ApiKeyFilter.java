package com.odissey.tourai.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odissey.tourai.dto.CustomErrorResponse;
import com.odissey.tourai.repository.ClientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor @Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final ClientRepository clientRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException{

        final String apiKey = request.getHeader("X-Api-Key");

        if (apiKey == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                    "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#401",
                    HttpStatus.UNAUTHORIZED,
                    request.getRequestURI(),
                    "ACCESSO NON AUTORIZZATO - Api Key mancante"
            );
            String jsonError = objectMapper.writeValueAsString(customErrorResponse);
            response.getWriter().write(jsonError);
            return;
        }

        if(clientRepository.existsByApiKeyAndExpirationDateAfter(apiKey, LocalDate.now())){
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                    "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#401",
                    HttpStatus.UNAUTHORIZED,
                    request.getRequestURI(),
                    "ACCESSO NON AUTORIZZATO - Api Key earrata o scaduta"
            );
            String jsonError = objectMapper.writeValueAsString(customErrorResponse);
            response.getWriter().write(jsonError);
            return;
        }
    }
}
