package com.fitness_tracker.Fitness_Tracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("HttpMessageNotReadableException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Required request body is missing.", null, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logger.error("Validation failed: {}", errors);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errors, null, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMessage().contains("Duplicate entry")
                ? "This email is already registered. Please use a different email or log in to your account."
                : e.getMessage();
        logger.error("DataIntegrityViolationException: {}", message);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(message, null, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Unhandled exception: {}", e.getMessage(), e);
        String errorMessage = e.getMessage() != null ? e.getMessage() + "." : "An unexpected error occurred.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
