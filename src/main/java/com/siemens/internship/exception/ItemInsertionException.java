package com.siemens.internship.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ItemInsertionException extends RuntimeException {
    private String errorMessage;
    
    public ItemInsertionException(BindingResult bindingResult) {
        StringBuilder messageBuilder = new StringBuilder("Validation failed: ");
        bindingResult.getFieldErrors().forEach(error ->
                messageBuilder.append(error.getField())
                        .append(" ")
                        .append(error.getDefaultMessage())
                        .append(", ")
        );
        this.errorMessage = messageBuilder.substring(0, messageBuilder.length() - 2);
    }
}
