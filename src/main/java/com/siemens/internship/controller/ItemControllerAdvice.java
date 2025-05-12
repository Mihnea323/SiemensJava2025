package com.siemens.internship.controller;

import com.siemens.internship.exception.ItemInsertionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ItemControllerAdvice {
    @ExceptionHandler(ItemInsertionException.class)
    public ResponseEntity<String> handleItemInsertionException(ItemInsertionException e) {
        return ResponseEntity.badRequest().body(e.getErrorMessage());
    }
}
