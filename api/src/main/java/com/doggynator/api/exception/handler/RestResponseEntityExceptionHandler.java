/* (C) Doggynator 2022 */
package com.doggynator.api.exception.handler;

import java.util.stream.Collectors;

import com.doggynator.api.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  public RestResponseEntityExceptionHandler() {
    super();
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.error("400 Status Code", ex);
    final var result = ex.getBindingResult();

    var error =
        result.getAllErrors().stream()
            .map(
                e -> {
                  if (e instanceof FieldError) {
                    return ((FieldError) e).getField() + " : " + e.getDefaultMessage();
                  } else {
                    return e.getObjectName() + " : " + e.getDefaultMessage();
                  }
                })
            .collect(Collectors.joining(", "));
    return handleExceptionInternal(
        ex, new ApiResponse(false, error), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
