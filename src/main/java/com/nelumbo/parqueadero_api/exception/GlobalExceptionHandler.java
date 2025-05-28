package com.nelumbo.parqueadero_api.exception;


import com.nelumbo.parqueadero_api.dto.errors.ErrorDetailDTO;
import com.nelumbo.parqueadero_api.dto.errors.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseError(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Invalid JSON format: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "404",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(UnauthorizedActionException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "403",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "400",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRule(BusinessRuleException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "400", ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorDetailDTO> errors = ex.getFieldErrors().stream()
                .map(fieldError -> new ErrorDetailDTO(
                        "400",
                        fieldError.getDefaultMessage(),
                        fieldError.getField()
                ))
                .toList();

        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(null, errors));
    }


    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String code, String message, String field) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseDTO(
                        null,
                        List.of(new ErrorDetailDTO(code, message, field))
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorDetailDTO> errors = ex.getConstraintViolations().stream()
                .map(v -> new ErrorDetailDTO(
                        "400",  // Bad Request
                        v.getMessage(),
                        v.getPropertyPath().toString().split("\\.")[1]))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(null, errors));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailExists(EmailAlreadyExistsException ex) {
        ErrorDetailDTO error = new ErrorDetailDTO(
                "400", // "USER_001"
                ex.getMessage(),
                ex.getFieldName() // "email"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(null, List.of(error)));
    }

    @ExceptionHandler(FieldAwareResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleFieldAwareException(FieldAwareResponseStatusException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                String.valueOf(ex.getStatusCode().value()),
                ex.getReason(),
                ex.getField()
        );

        return new ResponseEntity<>(
                new ErrorResponseDTO(null, List.of(errorDetail)),
                ex.getStatusCode()
        );
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(BusinessException ex) {
        String errorCode = ex.getMessage().contains("rol SOCIO") ? "403" : "400";

        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                errorCode,
                ex.getMessage(),
                ex.getField()
        );

        ErrorResponseDTO response = new ErrorResponseDTO(
                null,
                List.of(errorDetail)
        );
        HttpStatus httpStatus = errorCode.equals("403") ? HttpStatus.FORBIDDEN : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationFailed(AuthenticationFailedException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "401",
                ex.getMessage(),
                ex.getField()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalError(Exception ex) {
        log.error("",ex);
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "500",
                "Internal server error",
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "403",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        List<ErrorDetailDTO> errors = new ArrayList<>();

        // Extraemos los argumentos del mensaje de error
        Object[] detailArgs = ex.getDetailMessageArguments();
        if (detailArgs != null) {
            for (Object arg : detailArgs) {
                if (arg instanceof String str) {
                    errors.add(new ErrorDetailDTO(
                            "400",
                            str,
                            extractParameterName(ex)
                    ));
                }
            }
        }

        // Si no encontramos detalles, usamos el mensaje general
        if (errors.isEmpty()) {
            ex.getMessage();
            errors.add(new ErrorDetailDTO(
                    "400",
                    ex.getMessage(),
                    null
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(null, errors));
    }

    private String extractParameterName(HandlerMethodValidationException ex) {
        try {
            // Intento reflexivo para obtener el nombre del parámetro en versiones antiguas
            MethodParameter[] parameters = (MethodParameter[]) ex.getClass()
                    .getMethod("getMethodParameters").invoke(ex);
            if (parameters != null && parameters.length > 0) {
                return parameters[0].getParameterName();
            }
        } catch (Exception e) {
            // Fallback si falla la reflexión
            return null;
        }
        return null;
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(IllegalStateException ex) {
        ErrorDetailDTO response = new ErrorDetailDTO(
                "409",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(null, List.of(response)));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingHeaders(MissingRequestHeaderException ex) {
        ErrorDetailDTO error = new ErrorDetailDTO(
                "400",
                "El header " + ex.getHeaderName() + " es requerido",
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(null, List.of(error)));
    }

}