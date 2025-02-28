package org.dev.filerouge.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, List<String>> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();

            if (!errors.containsKey(fieldName)) {
                List<String> errorMessages = new ArrayList<>();
                errorMessages.add(error.getDefaultMessage());
                errors.put(fieldName, errorMessages);
            } else {
                errors.get(fieldName).add(error.getDefaultMessage());
            }
        });
        return errors;
    }
}