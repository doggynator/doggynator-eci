/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.util.Map;
import java.util.Optional;

import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.dto.SignUpRequest;
import com.doggynator.api.exception.UserAlreadyExistAuthenticationException;
import com.doggynator.api.model.AppUser;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

public interface UserService {

  public AppUser registerNewUser(SignUpRequest signUpRequest)
      throws UserAlreadyExistAuthenticationException;

  Optional<AppUser> findUserByEmail(String email);

  Optional<AppUser> findUserById(Long id);

  LocalUser processUserRegistration(
      String registrationId,
      Map<String, Object> attributes,
      OidcIdToken idToken,
      OidcUserInfo userInfo);
}
