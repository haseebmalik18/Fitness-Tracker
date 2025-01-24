package com.fitness_tracker.Fitness_Tracker.features.authentication.dto;

/**
 * Data Transfer Object (DTO) for authentication responses.
 * Used to provide feedback and authentication tokens after login or registration.
 */
public class AuthenticationResponseBody {

    /**
     * The authentication token provided to the user upon successful login or registration.
     */
    private final String token;

    /**
     * A message providing additional information about the authentication process.
     */
    private final String message;

    /**
     * Constructs an AuthenticationResponseBody.
     *
     * @param token   The authentication token.
     * @param message A message describing the result of the authentication process.
     */
    public AuthenticationResponseBody(String token, String message) {
        this.token = token;
        this.message = message;
    }

    /**
     * Retrieves the authentication token.
     *
     * @return The authentication token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Retrieves the response message.
     *
     * @return The response message.
     */
    public String getMessage() {
        return message;
    }

}
