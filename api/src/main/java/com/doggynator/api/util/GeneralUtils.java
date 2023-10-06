/* (C) Doggynator 2022 */
package com.doggynator.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.dto.SocialProvider;
import com.doggynator.api.dto.UserInfoResponse;
import com.doggynator.api.model.AppRole;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralUtils {

  public static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(
      final Set<AppRole> roles) {
    var authorities = new ArrayList<SimpleGrantedAuthority>();
    for (var role : roles) {
      authorities.add(new SimpleGrantedAuthority(role.getName().name()));
    }
    return authorities;
  }

  public static SocialProvider toSocialProvider(final String providerId) {
    for (var socialProvider : SocialProvider.values()) {
      if (socialProvider.getProviderType().equals(providerId)) {
        return socialProvider;
      }
    }
    return SocialProvider.LOCAL;
  }

  public static UserInfoResponse buildUserInfo(final LocalUser localUser) {
    var roles =
        localUser.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    var user = localUser.getUser();

    return new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), roles);
  }
}
