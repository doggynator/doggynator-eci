/* (C) Doggynator 2022 */
package com.doggynator.api.advice;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ErrorMessage {
  private final int statusCode;
  private final Date timestamp;
  private final String message;
  private final String description;
}
