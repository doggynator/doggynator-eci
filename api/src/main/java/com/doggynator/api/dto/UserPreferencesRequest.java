/* (C) Doggynator 2022 */
package com.doggynator.api.dto;

import lombok.Data;

@Data
public class UserPreferencesRequest {
  String option;
  String value;
}
