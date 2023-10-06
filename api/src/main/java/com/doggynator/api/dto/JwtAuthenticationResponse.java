/* (C) Doggynator 2022 */
package com.doggynator.api.dto;

import lombok.Value;

@Value public class JwtAuthenticationResponse {
  private String accessToken;
  private UserInfoResponse user;
}
