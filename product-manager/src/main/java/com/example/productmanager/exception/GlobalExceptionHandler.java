package com.example.productmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(
                error -> errorMap.put(error.getField(), error.getDefaultMessage())
        );
        return errorMap;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBusinessException(CategoryNotFoundException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Error message", e.getMessage());

        return errorMap;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Error message", e.getValue() + " isn't UUID");

        return errorMap;
    }

    @ExceptionHandler(CategoryExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCategoryExistException(CategoryExistException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Error message", e.getMessage());
        return errorMap;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCategoryExistException(ProductNotFoundException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Error message", e.getMessage());
        return errorMap;
    }

    @ExceptionHandler(ExistProductNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCategoryExistException(ExistProductNameException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Error message", e.getMessage());
        return errorMap;
    }
}
