/* (C) Doggynator 2022 */
package com.doggynator.api.advice;

import java.time.Instant;
import java.util.Date;

import com.doggynator.api.exception.TokenRefreshException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class TokenControllerAdvice {

  @ExceptionHandler(value = TokenRefreshException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ErrorMessage> handleTokenRefreshException(
      TokenRefreshException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ErrorMessage.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(Date.from(Instant.now()))
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build());
  }
}
