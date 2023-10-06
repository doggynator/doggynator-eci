/* (C) Doggynator 2022 */
package com.doggynator.api.security.oauth2.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.util.StringUtils;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {
  public FacebookOAuth2UserInfo(final Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return (String) attributes.get("id");
  }

  @Override
  public String getName() {
    return (String) attributes.get("name");
  }

  @Override
  public String getEmail() {
    return Optional.ofNullable(attributes.get("email"))
        .map(Object::toString)
        .filter(StringUtils::hasText)
        .orElse(String.format("%s@%s", getId(), "facebook.com"));
  }

  @Override
  @SuppressWarnings("unchecked")
  public String getImageUrl() {
    if (attributes.containsKey("picture")) {
      var pictureObj = (Map<String, Object>) attributes.get("picture");
      if (pictureObj.containsKey("data")) {
        var dataObj = (Map<String, Object>) pictureObj.get("data");
        if (dataObj.containsKey("url")) {
          return (String) dataObj.get("url");
        }
      }
    }
    return null;
  }
}
