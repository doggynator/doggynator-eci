/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.exception.ResourceNotFoundException;
import com.doggynator.api.model.AppUser;
import com.doggynator.api.security.jwt.JWTManager;
import com.doggynator.api.util.GeneralUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("localUserDetailService")
public class LocalUserDetailService implements UserDetailsService {

  @Autowired private UserService userService;
  @Autowired private JWTManager jwtManager;

  @Override
  @Transactional
  public LocalUser loadUserByUsername(final String email) throws UsernameNotFoundException {
    return userService
        .findUserByEmail(email)
        .map(this::createLocalUser)
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    "Usuario con correo electrónico " + email + " no esta registrado"));
  }

  @Transactional
  public LocalUser loadUserById(final Long id) {
    var user =
        userService
            .findUserById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    return createLocalUser(user);
  }

  private LocalUser createLocalUser(final AppUser user) {
    return new LocalUser(
        user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        true,
        true,
        true,
        GeneralUtils.buildSimpleGrantedAuthorities(user.getRoles()),
        user);
  }

  @Transactional
  public LocalUser loadUserByToken(final String token) {
    var email = jwtManager.getEmailFromJwtToken(token);
    var user =
        userService
            .findUserByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
    return createLocalUser(user);
  }
}
