/* (C) Doggynator 2022 */
package com.doggynator.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

  public BadRequestException(final String message) {
    super(message);
  }

  public BadRequestException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
