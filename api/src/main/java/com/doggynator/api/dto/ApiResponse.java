/* (C) Doggynator 2022 */
package com.doggynator.api.dto;

import lombok.Value;

@Value public class ApiResponse {
  private Boolean success;
  private String message;
}
