package com.fitness_tracker.Fitness_Tracker.features.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for authentication requests.
 * Used for login and registration endpoints.
 */
public class AuthenticationRequestBody {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private final String email;

    @NotBlank(message = "Password cannot be empty")
    private final String password;

    /**
     * Constructor for AuthenticationRequestBody.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     */
    public AuthenticationRequestBody(String email, String password) {
        this.email = email.trim(); // Ensure no accidental leading/trailing spaces
        this.password = password; // Password should remain as-is for security
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
