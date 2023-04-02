package com.news.portal.entity.response;

import lombok.Data;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Data
public class MethodArgumentNotValidResponse {

    private Map<String, String> errors = new HashMap<>();

    public MethodArgumentNotValidResponse(MethodArgumentNotValidException exception) {
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
    }
}