package com.abs.app.common.exception;

import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.abs.app.common.response.ErrorResponse;
import com.abs.app.common.response.ValidationErrorResponse;

import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalException {

        private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ValidationErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new LinkedHashMap<>();

                for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                        errors.put(error.getField(), error.getDefaultMessage());
                }

                return ResponseEntity.badRequest()
                                .body(new ValidationErrorResponse(
                                                false,
                                                "Validation failed",
                                                errors));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException ex) {

                Throwable cause = ex.getMostSpecificCause();

                if (cause instanceof DateTimeParseException) {
                        return ResponseEntity.badRequest().body(
                                        new ErrorResponse(
                                                        false,
                                                        "Ngày không tồn tại hoặc sai định dạng."));
                }

                return ResponseEntity.badRequest().body(
                                new ErrorResponse(
                                                false,
                                                "Dữ liệu không hợp lệ."));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex) {

                return ResponseEntity.badRequest()
                                .body(new ErrorResponse(
                                                false,
                                                ex.getMessage()));
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(
                        BusinessException ex) {

                return ResponseEntity.badRequest()
                                .body(new ErrorResponse(
                                                false,
                                                ex.getMessage()));
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ErrorResponse> handleUnauthorizedException(
                        UnauthorizedException ex) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ErrorResponse(
                                                false,
                                                ex.getMessage()));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponse(
                                                false,
                                                ex.getMessage()));
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
                        DuplicateResourceException ex) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse(
                                                false,
                                                ex.getMessage()));
        }

        @ExceptionHandler({
                        DataAccessException.class,
                        PersistenceException.class,
                        SQLException.class
        })
        public ResponseEntity<ErrorResponse> handleDatabaseException(
                        Exception ex) {

                log.error("Database exception", ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponse(
                                                false,
                                                INTERNAL_SERVER_ERROR_MESSAGE));
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex) {

                log.error("Unhandled runtime exception", ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponse(
                                                false,
                                                INTERNAL_SERVER_ERROR_MESSAGE));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(
                        Exception ex) {

                log.error("Unhandled exception", ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponse(
                                                false,
                                                INTERNAL_SERVER_ERROR_MESSAGE));
        }
}
