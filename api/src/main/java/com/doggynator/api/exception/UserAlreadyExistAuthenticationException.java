/* (C) Doggynator 2022 */
package com.doggynator.api.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAlreadyExistAuthenticationException extends AuthenticationException {

  public UserAlreadyExistAuthenticationException(final String msg) {
    super(msg);
  }
}
