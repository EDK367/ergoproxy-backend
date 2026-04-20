package com.ergoproxy.backend.exception;

import com.ergoproxy.backend.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(ex.getStatus(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
