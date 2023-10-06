/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.dto.SignUpRequest;
import com.doggynator.api.dto.SocialProvider;
import com.doggynator.api.enums.USettingsStage;
import com.doggynator.api.exception.OAuth2AuthenticationProcessingException;
import com.doggynator.api.exception.UserAlreadyExistAuthenticationException;
import com.doggynator.api.model.AppRole;
import com.doggynator.api.model.AppUser;
import com.doggynator.api.model.Role;
import com.doggynator.api.model.UserStage;
import com.doggynator.api.model.Usuario;
import com.doggynator.api.repo.RoleRepository;
import com.doggynator.api.repo.UserRepository;
import com.doggynator.api.repo.UserStageRepository;
import com.doggynator.api.repo.UsuarioRepository;
import com.doggynator.api.security.oauth2.user.OAuth2UserInfo;
import com.doggynator.api.security.oauth2.user.OAuth2UserInfoFactory;
import com.doggynator.api.util.GeneralUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private UserStageRepository ustageRepository;

  @Autowired private UsuarioRepository uPreferencesRepository;

  @Override
  @Transactional(value = "transactionManager")
  public AppUser registerNewUser(final SignUpRequest signUpRequest)
      throws UserAlreadyExistAuthenticationException {
    if (signUpRequest.getUserID() != null && userRepository.existsById(signUpRequest.getUserID())) {
      throw new UserAlreadyExistAuthenticationException(
          "Usuario con identificación "
              + signUpRequest.getUserID()
              + " ya se encuentra registrado");
    } else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new UserAlreadyExistAuthenticationException(
          "Usuario con correo electrónico "
              + signUpRequest.getEmail()
              + " ya se encuentra registrado");
    }
    var user = buildUser(signUpRequest);
    var now = Calendar.getInstance().getTime();
    user.setCreatedDate(now);
    user.setModifiedDate(now);
    user = userRepository.save(user);
    userRepository.flush();
    log.info("USUARIO ** {}", user.getId());
    UserStage ustage = new UserStage(user.getId(), USettingsStage.FORM.getStage());
    ustageRepository.save(ustage);
    Usuario upreferences = new Usuario();
    upreferences.setUserid(user.getId());
    upreferences.setFpy(0); // es un usuario sin facebook, no carga nada
    uPreferencesRepository.save(upreferences);
    return user;
  }

  private AppUser buildUser(final SignUpRequest formDTO) {
    AppUser user = new AppUser();
    user.setUsername(formDTO.getUsername());
    user.setEmail(formDTO.getEmail());
    user.setPassword(passwordEncoder.encode(formDTO.getPassword()));
    final var roles = new HashSet<AppRole>();
    roles.add(roleRepository.findByName(Role.USER));
    user.setRoles(roles);
    user.setProvider(formDTO.getSocialProvider().getProviderType());
    user.setEnabled(true);
    user.setProviderUserId(formDTO.getProviderUserId());
    return user;
  }

  @Override
  public Optional<AppUser> findUserByEmail(final String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  @Transactional
  public LocalUser processUserRegistration(
      final String registrationId,
      final Map<String, Object> attributes,
      final OidcIdToken idToken,
      OidcUserInfo userInfo) {
    var oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
    if (!StringUtils.hasText(oAuth2UserInfo.getName())) {
      throw new OAuth2AuthenticationProcessingException(
          "Nombre no encontrado en el servicio de autenticación OAuth2");
    }

    var userDetails = toUserRegistrationObject(registrationId, oAuth2UserInfo);
    var user = findUserByEmail(userDetails.getEmail());

    if (user.isPresent()) {
      var userGet = user.get();
      if (!userGet.getProvider().equals(registrationId)
          && !userGet.getProvider().equals(SocialProvider.LOCAL.getProviderType())) {
        throw new OAuth2AuthenticationProcessingException(
            "Esta registrado con la cuenta de "
                + userGet.getProvider()
                + ". Por favor utilice su cuenta de "
                + userGet.getProvider()
                + " para ingresar.");
      }
      user = Optional.of(updateExistingUser(userGet, oAuth2UserInfo));
    } else {
      user = Optional.of(registerNewUser(userDetails));
    }

    return LocalUser.create(user.get(), attributes, idToken, userInfo);
  }

  private AppUser updateExistingUser(
      final AppUser existingUser, final OAuth2UserInfo oAuth2UserInfo) {
    existingUser.setUsername(oAuth2UserInfo.getName());
    return userRepository.save(existingUser);
  }

  private SignUpRequest toUserRegistrationObject(
      final String registrationId, final OAuth2UserInfo oAuth2UserInfo) {
    return SignUpRequest.builder()
        .providerUserId(oAuth2UserInfo.getId())
        .username(oAuth2UserInfo.getName())
        .email(oAuth2UserInfo.getEmail())
        .socialProvider(GeneralUtils.toSocialProvider(registrationId))
        .password(oAuth2UserInfo.getId())
        .build();
  }

  @Override
  public Optional<AppUser> findUserById(Long id) {
    return userRepository.findById(id);
  }
}
