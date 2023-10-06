/* (C) Doggynator 2022 */
package com.doggynator.api.config;

import com.doggynator.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityBeansConfig {

  /*
   * By default, Spring OAuth2 uses
   * HttpSessionOAuth2AuthorizationRequestRepository to save the authorization
   * request. But, since our service is stateless, we can't save it in the
   * session. We'll save the request in a Base64 encoded cookie instead.
   */
  @Bean
  public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}
