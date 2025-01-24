package com.fitness_tracker.Fitness_Tracker.features.authentication.controller;

import com.fitness_tracker.Fitness_Tracker.features.authentication.dto.AuthenticationRequestBody;
import com.fitness_tracker.Fitness_Tracker.features.authentication.dto.AuthenticationResponseBody;
import com.fitness_tracker.Fitness_Tracker.features.authentication.model.AuthenticationUser;
import com.fitness_tracker.Fitness_Tracker.features.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/user")
    public ResponseEntity<AuthenticationUser> getUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        // Fetch and return the authenticated user's details
        AuthenticationUser fetchedUser = authenticationService.getUser(user.getEmail());
        return ResponseEntity.ok(fetchedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseBody> login(@Valid @RequestBody AuthenticationRequestBody loginRequestBody) {
        // Process login and return authentication details
        AuthenticationResponseBody response = authenticationService.login(loginRequestBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseBody> register(@Valid @RequestBody AuthenticationRequestBody registerRequestBody) {
        // Handle user registration
        AuthenticationResponseBody response = authenticationService.registerUser(registerRequestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/validate-email-verification-token")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @RequestParam String token,
            @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        // Validate the email verification token
        authenticationService.validateEmailVerificationToken(token, user.getEmail());
        return ResponseEntity.ok(Map.of("message", "Email verified successfully."));
    }

    @GetMapping("/send-email-verification-token")
    public ResponseEntity<Void> sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        // Send an email verification token
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return ResponseEntity.noContent().build(); // 204 No Content as there's no response body
    }

    @PutMapping("/send-password-reset-token")
    public ResponseEntity<Void> sendPasswordResetToken(@RequestParam @Email String email) {
        // Send a password reset token to the provided email
        authenticationService.sendPasswordResetToken(email);
        return ResponseEntity.noContent().build(); // 204 No Content as there's no response body
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String newPassword,
            @RequestParam String token,
            @RequestParam @Email String email) {
        // Reset the user's password using the provided token
        authenticationService.resetPassword(email, newPassword, token);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
    }

}
