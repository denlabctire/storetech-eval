package com.cantire.storetech.evaluation.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            IllegalArgumentException.class })
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorPayload("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorPayload("INTERNAL_SERVER_ERROR", ex.getMessage()));
    }

    private Map<String, String> errorPayload(String error, String message) {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("error", error);
        payload.put("message", message);
        return payload;
    }
}
