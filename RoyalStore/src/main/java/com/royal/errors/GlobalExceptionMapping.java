package com.royal.errors;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionMapping {
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> handleHttpException(HttpException e) {
        log.info("HTTP Response " + e.getHttpErrorCode().toString() + ": " + e.getMessage());
        return ResponseEntity.status(e.getHttpErrorCode().value()).body(e.getMessage());
    }
}
