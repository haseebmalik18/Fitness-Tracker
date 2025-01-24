package com.fitness_tracker.Fitness_Tracker.features.authentication.service;

import com.fitness_tracker.Fitness_Tracker.features.authentication.dto.AuthenticationRequestBody;
import com.fitness_tracker.Fitness_Tracker.features.authentication.dto.AuthenticationResponseBody;
import com.fitness_tracker.Fitness_Tracker.features.authentication.model.AuthenticationUser;
import com.fitness_tracker.Fitness_Tracker.features.authentication.repository.AuthenticationUserRepository;
import com.fitness_tracker.Fitness_Tracker.features.authentication.utils.EmailService;
import com.fitness_tracker.Fitness_Tracker.features.authentication.utils.Encoder;
import com.fitness_tracker.Fitness_Tracker.features.authentication.utils.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static final int TOKEN_EXPIRY_DURATION_MINUTES = 3;

    private final JsonWebToken jsonWebToken;
    private final Encoder encoder;
    private final AuthenticationUserRepository authenticationUserRepository;
    private final EmailService emailService;

    public AuthenticationService(JsonWebToken jsonWebToken, Encoder encoder, AuthenticationUserRepository authenticationUserRepository, EmailService emailService) {
        this.jsonWebToken = jsonWebToken;
        this.encoder = encoder;
        this.authenticationUserRepository = authenticationUserRepository;
        this.emailService = emailService;
    }

    private static String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        return String.valueOf(random.nextInt(90000) + 10000); // Generate a 5-digit token
    }

    public void sendEmailVerificationToken(String email) {
        Optional<AuthenticationUser> userOptional = authenticationUserRepository.findByEmail(email);

        if (userOptional.isEmpty() || userOptional.get().getEmailVerified()) {
            throw new IllegalArgumentException("Email verification token failed, or email is already verified.");
        }

        AuthenticationUser user = userOptional.get();
        String emailVerificationToken = generateSecureToken();
        String hashedToken = encoder.encode(emailVerificationToken);

        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_DURATION_MINUTES));
        authenticationUserRepository.save(user);

        String subject = "Verify Your Email - Fitness Tracker";
        String body = String.format("""
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #ffffff; background-color: #121212; padding: 20px;">
            <div style="text-align: center;">
                <h1 style="color: #5cb85c;">Welcome to Fitness Tracker!</h1>
                <p style="font-size: 16px; color: #dddddd;">Thank you for signing up. You're just one step away from activating your account.</p>
                <p style="font-size: 16px; color: #ffffff;">Here is your email verification code:</p>
                <h2 style="color: #0275d8;">%s</h2>
                <p style="font-size: 16px; color: #dddddd;">This code will expire in <strong>%d minutes</strong>.</p>
                <p style="font-size: 14px; margin-top: 20px; color: #888888;">
                    If you did not sign up for Fitness Tracker, please ignore this email.
                </p>
            </div>
        </body>
        </html>
        """, emailVerificationToken, TOKEN_EXPIRY_DURATION_MINUTES);



        try {
            emailService.sendEmail(email, subject, body);
        } catch (Exception e) {
            logger.error("Error while sending email verification token: {}", e.getMessage());
        }
    }

    public void validateEmailVerificationToken(String token, String email) {
        Optional<AuthenticationUser> userOptional = authenticationUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Email verification token failed.");
        }

        AuthenticationUser user = userOptional.get();
        if (encoder.matches(token, user.getEmailVerificationToken()) && user.getEmailVerificationTokenExpiryDate().isAfter(LocalDateTime.now())) {
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            user.setEmailVerificationTokenExpiryDate(null);
            authenticationUserRepository.save(user);
        } else if (user.getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Email verification token expired.");
        } else {
            throw new IllegalArgumentException("Email verification token failed.");
        }
    }

    public AuthenticationUser getUser(String email) {
        return authenticationUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    public AuthenticationResponseBody registerUser(AuthenticationRequestBody registerRequestBody) {
        AuthenticationUser user = new AuthenticationUser(registerRequestBody.getEmail(), encoder.encode(registerRequestBody.getPassword()));
        authenticationUserRepository.save(user);

        sendEmailVerificationToken(registerRequestBody.getEmail());

        String authToken = jsonWebToken.generateToken(registerRequestBody.getEmail());
        return new AuthenticationResponseBody(authToken, "User registered successfully.");
    }

    public AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody) {
        AuthenticationUser user = authenticationUserRepository.findByEmail(loginRequestBody.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!encoder.matches(loginRequestBody.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        String token = jsonWebToken.generateToken(user.getEmail());
        return new AuthenticationResponseBody(token, "Login successful.");
    }

    public void sendPasswordResetToken(String email) {
        Optional<AuthenticationUser> userOptional = authenticationUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        AuthenticationUser user = userOptional.get();
        String passwordResetToken = generateSecureToken();
        String hashedToken = encoder.encode(passwordResetToken);

        user.setPasswordResetToken(hashedToken);
        user.setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_DURATION_MINUTES));
        authenticationUserRepository.save(user);

        String subject = "Password Reset Request - Fitness Tracker";
        String body = String.format("""
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #ffffff; background-color: #121212; padding: 20px;">
            <div style="text-align: center;">
                <h1 style="color: #d9534f;">Password Reset Request</h1>
                <p style="font-size: 16px; color: #dddddd;">We received a request to reset your password for your Fitness Tracker account.</p>
                <p style="font-size: 16px; color: #ffffff;">Here is your password reset code:</p>
                <h2 style="color: #0275d8;">%s</h2>
                <p style="font-size: 16px; color: #dddddd;">This code will expire in <strong>%d minutes</strong>.</p>
                <p style="font-size: 14px; margin-top: 20px; color: #888888;">
                    If you did not request this password reset, please ignore this email.
                </p>
            </div>
        </body>
        </html>
        """, passwordResetToken, TOKEN_EXPIRY_DURATION_MINUTES);


        try {
            emailService.sendEmail(email, subject, body);
        } catch (Exception e) {
            logger.error("Error while sending password reset token: {}", e.getMessage());
        }
    }

    public void resetPassword(String email, String newPassword, String token) {
        Optional<AuthenticationUser> userOptional = authenticationUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Password reset token failed.");
        }

        AuthenticationUser user = userOptional.get();
        if (encoder.matches(token, user.getPasswordResetToken()) && user.getPasswordResetTokenExpiryDate().isAfter(LocalDateTime.now())) {
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiryDate(null);
            user.setPassword(encoder.encode(newPassword));
            authenticationUserRepository.save(user);
        } else if (user.getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token expired.");
        } else {
            throw new IllegalArgumentException("Password reset token failed.");
        }
    }
}
