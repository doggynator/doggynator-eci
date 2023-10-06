/* (C) Doggynator 2022 */
package com.doggynator.api.security.oauth2.user;

import java.util.Map;

import com.doggynator.api.dto.SocialProvider;
import com.doggynator.api.exception.OAuth2AuthenticationProcessingException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2UserInfoFactory {
  public static OAuth2UserInfo getOAuth2UserInfo(
      final String registrationId, final Map<String, Object> attributes) {
    if (registrationId.equalsIgnoreCase(SocialProvider.FACEBOOK.getProviderType())) {
      return new FacebookOAuth2UserInfo(attributes);
    } else {
      throw new OAuth2AuthenticationProcessingException(
          "Lo Sentimos! Ingreso con " + registrationId + " aun no esta soportado.");
    }
  }
}
