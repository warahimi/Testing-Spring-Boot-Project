package com.cwc.Unit_Integration_Testing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ProductGlobalExceptionHandler {
    private DateTimeFormatter dateTimeFormatter;
    public ProductGlobalExceptionHandler()
    {
        dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleProductNotFoundException(ProductNotFoundException e)
    {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter).toString())
                .error("Product Not Found")
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.toString())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
}
