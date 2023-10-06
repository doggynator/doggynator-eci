/* (C) Doggynator 2022 */
package com.doggynator.api.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.doggynator.api.model.AppUser;
import com.doggynator.api.util.GeneralUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class LocalUser extends User implements OidcUser {

  private final OidcIdToken idToken;
  private final OidcUserInfo userInfo;
  private Map<String, Object> attributes;
  private AppUser user;

  public LocalUser(
      final String userID,
      final String password,
      final boolean enabled,
      final boolean accountNonExpired,
      final boolean credentialsNonExpired,
      final boolean accountNonLocked,
      final Collection<? extends GrantedAuthority> authorities,
      final AppUser user) {
    this(
        userID,
        password,
        enabled,
        accountNonExpired,
        credentialsNonExpired,
        accountNonLocked,
        authorities,
        user,
        null,
        null);
  }

  public LocalUser(
      final String userID,
      final String password,
      final boolean enabled,
      final boolean accountNonExpired,
      final boolean credentialsNonExpired,
      final boolean accountNonLocked,
      final Collection<? extends GrantedAuthority> authorities,
      final AppUser user,
      final OidcIdToken idToken,
      final OidcUserInfo userInfo) {
    super(
        userID,
        password,
        enabled,
        accountNonExpired,
        credentialsNonExpired,
        accountNonLocked,
        authorities);
    this.user = user;
    this.idToken = idToken;
    this.userInfo = userInfo;
    if (Objects.isNull(this.attributes)) {
      this.attributes = new HashMap<>();
    }
    this.attributes.put("id", user.getId());
    this.attributes.put("password", user.getPassword());
    this.attributes.put("username", user.getUsername());
    this.attributes.put("provider", user.getProvider());
    this.attributes.put("email", user.getEmail());
  }

  public static LocalUser create(
      final com.doggynator.api.model.AppUser user,
      final Map<String, Object> attributes,
      final OidcIdToken idToken,
      final OidcUserInfo userInfo) {
    LocalUser localUser =
        new LocalUser(
            user.getEmail(),
            user.getPassword(),
            user.isEnabled(),
            true,
            true,
            true,
            GeneralUtils.buildSimpleGrantedAuthorities(user.getRoles()),
            user,
            idToken,
            userInfo);
    localUser.setAttributes(attributes);
    return localUser;
  }

  public Long getId() {
    return this.user.getId();
  }

  @Override
  public String getName() {
    return this.user.getUsername();
  }

  @Override
  public Map<String, Object> getAttributes() {
    return this.attributes;
  }

  @Override
  public Map<String, Object> getClaims() {
    return this.attributes;
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return this.userInfo;
  }

  @Override
  public OidcIdToken getIdToken() {
    return this.idToken;
  }

  public AppUser getUser() {
    return user;
  }
}
