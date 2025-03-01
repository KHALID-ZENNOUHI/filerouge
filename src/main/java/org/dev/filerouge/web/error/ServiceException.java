package org.dev.filerouge.web.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public ServiceException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public ServiceException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    // ResourceNotFoundException
    public static class ResourceNotFoundException extends ServiceException {
        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue),
                    HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
        }
    }

    // ValidationException
    public static class ValidationException extends ServiceException {
        public ValidationException(String message) {
            super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
    }

    // DuplicateResourceException
    public static class DuplicateResourceException extends ServiceException {
        public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue),
                    HttpStatus.CONFLICT, "RESOURCE_ALREADY_EXISTS");
        }
    }

    // OperationNotAllowedException
    public static class OperationNotAllowedException extends ServiceException {
        public OperationNotAllowedException(String message) {
            super(message, HttpStatus.FORBIDDEN, "OPERATION_NOT_ALLOWED");
        }
    }
}