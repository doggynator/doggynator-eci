/* (C) Doggynator 2022 */
package com.doggynator.api.security.oauth2;

import com.doggynator.api.exception.OAuth2AuthenticationProcessingException;
import com.doggynator.api.service.UserService;
import com.doggynator.api.service.facebook.FacebookUserDataHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  @Autowired private UserService userService;
  @Autowired private FacebookUserDataHandler facebookUserDataHandler;

  @Override
  public OAuth2User loadUser(final OAuth2UserRequest oAuth2UserRequest)
      throws OAuth2AuthenticationException {
    var oAuth2User = super.loadUser(oAuth2UserRequest);
    try {
      var localUser =
          userService.processUserRegistration(
              oAuth2UserRequest.getClientRegistration().getRegistrationId(),
              oAuth2User.getAttributes(),
              null,
              null);
      // facebookUserDataHandler.handle(
      //    oAuth2UserRequest.getAccessToken().getTokenValue(), localUser.getId());
      return localUser;
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
