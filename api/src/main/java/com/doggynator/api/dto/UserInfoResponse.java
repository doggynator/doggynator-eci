/* (C) Doggynator 2022 */
package com.doggynator.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
  private Long id;
  private String username;
  private String email;
  private List<String> roles;
}
