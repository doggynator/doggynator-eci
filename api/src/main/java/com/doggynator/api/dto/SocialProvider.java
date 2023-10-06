/* (C) Doggynator 2022 */
package com.doggynator.api.dto;

public enum SocialProvider {
  FACEBOOK("facebook"),
  LOCAL("local");

  private String providerType;

  public String getProviderType() {
    return providerType;
  }

  SocialProvider(final String providerType) {
    this.providerType = providerType;
  }
}
