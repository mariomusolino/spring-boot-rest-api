package com.odissey.tour.configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Lista di endpoint pubblici
        List<String> publicEndpoints = Arrays.asList("/auth", "/swagger", "/api-docs");
        String requestPath = request.getRequestURI();

        // Permetti accesso agli endpoint pubblici
        if (publicEndpoints.stream().anyMatch(requestPath::contains)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // Se non c'è token su endpoint protetto -> 403
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                    "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#403",
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    "ACCESSO NEGATO"
            );
            String jsonError = objectMapper.writeValueAsString(customErrorResponse);
            response.getWriter().write(jsonError);
            return; // NON chiamare filterChain.doFilter()
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.parse(jwt).getSubject();

            if (!username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if(!userDetails.isEnabled()){
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                            "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#403",
                            HttpStatus.FORBIDDEN,
                            request.getRequestURI(),
                            "Utente disabilitato."
                    );
                    String jsonError = objectMapper.writeValueAsString(customErrorResponse);
                    response.getWriter().write(jsonError);
                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (JwtException ex){
            // Gestisci token non valido/scaduto
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                    "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#403",
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    "Token non valido o scaduto."
            );
            String jsonError = objectMapper.writeValueAsString(customErrorResponse);
            response.getWriter().write(jsonError);
        } catch (Exception e) {
            log.error(">>> ECCEZIONE IMPREVISTA: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                    "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#403",
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    "ACCESSO NEGATO"
            );
            String jsonError = objectMapper.writeValueAsString(customErrorResponse);
            response.getWriter().write(jsonError);
        }
    }
}