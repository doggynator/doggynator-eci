/* (C) Doggynator 2022 */
package com.doggynator.api.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.doggynator.api.validator.PasswordMatches;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@PasswordMatches
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

  private Long userID;

  private String providerUserId;

  @NotEmpty private String username;

  @NotEmpty private String email;

  private SocialProvider socialProvider;

  @Size(min = 6, message = "{Size.userDto.password}")
  private String password;

  @Size(min = 6, message = "{Size.userDto.password}")
  @NotEmpty
  private String matchingPassword;

  public SignUpRequest(
      String providerUserId,
      String username,
      String email,
      String password,
      SocialProvider socialProvider) {
    this.providerUserId = providerUserId;
    this.username = username;
    this.email = email;
    this.password = password;
    this.socialProvider = socialProvider;
  }
}
