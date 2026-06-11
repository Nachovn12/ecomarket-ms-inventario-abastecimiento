package com.ecomarket.inventario.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador centralizado de excepciones para ms-inventario-abastecimiento.
 * Retorna respuestas JSON estructuradas con timestamp, status, error, message y path.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SkuDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleSkuDuplicado(
            SkuDuplicadoException ex, HttpServletRequest req) {
        log.warn("SKU duplicado: {} - path: {}", ex.getMessage(), req.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(
            RecursoNoEncontradoException ex, HttpServletRequest req) {
        log.warn("Recurso no encontrado: {} - path: {}", ex.getMessage(), req.getRequestURI());
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Argumento inválido: {} - path: {}", ex.getMessage(), req.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("Error de validación en path: {} - campos: {}", req.getRequestURI(), errores);
        Map<String, Object> body = buildBodyMap(HttpStatus.BAD_REQUEST, "Validation Error",
                "Error de validación en los campos enviados", req.getRequestURI());
        body.put("validaciones", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(
            Exception ex, HttpServletRequest req) {
        log.error("Error interno no controlado - path: {}", req.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Error interno en el servidor", req.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error,
                                                               String message, String path) {
        return ResponseEntity.status(status).body(buildBodyMap(status, error, message, path));
    }

    private Map<String, Object> buildBodyMap(HttpStatus status, String error,
                                              String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return body;
    }
}
