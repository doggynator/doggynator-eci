/* (C) Doggynator 2022 */
package com.doggynator.api.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {

  public OAuth2AuthenticationProcessingException(final String msg, final Throwable t) {
    super(msg, t);
  }

  public OAuth2AuthenticationProcessingException(final String msg) {
    super(msg);
  }
}
