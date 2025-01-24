package com.fitness_tracker.Fitness_Tracker.features.authentication.filter;

import com.fitness_tracker.Fitness_Tracker.features.authentication.model.AuthenticationUser;
import com.fitness_tracker.Fitness_Tracker.features.authentication.service.AuthenticationService;
import com.fitness_tracker.Fitness_Tracker.features.authentication.utils.JsonWebToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class AuthenticationFilter extends HttpFilter {

    private static final List<String> UNSECURED_ENDPOINTS = List.of(
            "/api/v1/authentication/login",
            "/api/v1/authentication/register",
            "/api/v1/authentication/send-password-reset-token",
            "/api/v1/authentication/reset-password"
    );

    private final JsonWebToken jsonWebTokenService;
    private final AuthenticationService authenticationService;

    public AuthenticationFilter(JsonWebToken jsonWebTokenService, AuthenticationService authenticationService) {
        this.jsonWebTokenService = jsonWebTokenService;
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Add CORS headers (this should ideally be moved to global configuration, but retained here for simplicity)
        addCorsHeaders(response);

        // Handle preflight requests for CORS
        if (isPreflightRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();

        // Skip authentication for unsecured endpoints
        if (UNSECURED_ENDPOINTS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String authorization = request.getHeader("Authorization");

            // Validate the Authorization header
            if (isAuthorizationHeaderInvalid(authorization)) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or Token missing");
                return;
            }

            String token = authorization.substring(7); // Remove "Bearer " prefix
            validateToken(token);

            // Retrieve user and attach to the request
            String email = jsonWebTokenService.getEmailFromToken(token);
            AuthenticationUser user = authenticationService.getUser(email);

            if (user == null) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            request.setAttribute("authenticatedUser", user);
            chain.doFilter(request, response);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    // Helper method to add CORS headers
    private void addCorsHeaders(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    // Helper method to check if the request is a CORS preflight request
    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    // Validate Authorization header structure
    private boolean isAuthorizationHeaderInvalid(String authorization) {
        return authorization == null || !authorization.startsWith("Bearer ");
    }

    // Validate the JWT token
    private void validateToken(String token) {
        if (jsonWebTokenService.isTokenExpired(token)) {
            throw new IllegalArgumentException("Token expired");
        }
    }

    // Send a JSON-formatted error response
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}
