/* (C) Doggynator 2022 */
package com.doggynator.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
  private final String resourceName;
  private final String fieldName;
  private final Object fieldValue;

  public ResourceNotFoundException(
      final String resourceName, final String fieldName, final Object fieldValue) {
    super(String.format("%s no encontrado con %s : '%s'", resourceName, fieldName, fieldValue));
    this.resourceName = resourceName;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
