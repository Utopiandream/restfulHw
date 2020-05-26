package com.charter.restfulHw.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.charter.restfulHw.exception.NoDataFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(NoDataFoundException.class)
  public ResponseEntity<Object> handleNodataFoundException(NoDataFoundException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "No Transactions found");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

}
