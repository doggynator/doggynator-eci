/* (C) Doggynator 2022 */
package com.doggynator.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenRefreshException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenRefreshException(String token, String message) {
    super(String.format("Fallo para [%s]: %s", token, message));
  }
}
