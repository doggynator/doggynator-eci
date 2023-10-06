/* (C) Doggynator 2022 */
package com.doggynator.api.dto;

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
public class StringResponse {

  private String message;
}
