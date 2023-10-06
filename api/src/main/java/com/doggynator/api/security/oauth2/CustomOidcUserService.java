/* (C) Doggynator 2022 */
package com.doggynator.api.security.oauth2;

import com.doggynator.api.exception.OAuth2AuthenticationProcessingException;
import com.doggynator.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

  @Autowired private UserService userService;

  @Override
  public OidcUser loadUser(final OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    var oidcUser = super.loadUser(userRequest);
    try {
      return userService.processUserRegistration(
          userRequest.getClientRegistration().getRegistrationId(),
          oidcUser.getAttributes(),
          oidcUser.getIdToken(),
          oidcUser.getUserInfo());
    } catch (AuthenticationException ex) {
      throw ex;
    } catch (Exception ex) {
      ex.printStackTrace();
      // Throwing an instance of AuthenticationException will trigger the
      // OAuth2AuthenticationFailureHandler
      throw new OAuth2AuthenticationProcessingException(ex.getMessage(), ex.getCause());
    }
  }
}
