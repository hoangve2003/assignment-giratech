package com.example.productmanager.exception;

public class CategoryExistException extends RuntimeException {
    public CategoryExistException(String msg) {
        super(msg);
    }
}
