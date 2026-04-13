package com.electromart.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AppException.class, ResourceNotFoundException.class, IllegalArgumentException.class})
    public Object handleBusinessExceptions(RuntimeException exception, HttpServletRequest request, Model model) {
        HttpStatus status = exception instanceof ResourceNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        if (isApiRequest(request)) {
            return buildApiError(status, exception.getMessage(), request.getRequestURI());
        }
        log.warn("Business exception for path {}: {}", request.getRequestURI(), exception.getMessage());
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request, Model model) {
        String message = exception.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Please review the submitted details.");
        if (isApiRequest(request)) {
            return buildApiError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
        }
        log.warn("Validation exception for path {}: {}", request.getRequestURI(), message);
        model.addAttribute("errorMessage", message);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleUnhandledExceptions(Exception exception, HttpServletRequest request, Model model) {
        log.error("Unhandled exception for path {}", request.getRequestURI(), exception);
        if (isApiRequest(request)) {
            return buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on our side. Please try again later.", request.getRequestURI());
        }
        model.addAttribute("errorMessage", "Something went wrong on our side. Please try again later.");
        return "error";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        return path.startsWith("/order")
                || path.startsWith("/address")
                || (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE));
    }

    private ResponseEntity<Map<String, Object>> buildApiError(HttpStatus status, String message, String path) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now().toString());
        payload.put("status", status.value());
        payload.put("error", status.getReasonPhrase());
        payload.put("message", message);
        payload.put("path", path);
        return ResponseEntity.status(status).body(payload);
    }
}
