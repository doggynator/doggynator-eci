/* (C) Doggynator 2023 */
package com.doggynator.api.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserStage implements Serializable {

  public UserStage(Long userId, Integer stage) {
    this.userid = userId;
    this.stage = stage;
  }

  @Id private Long userid;

  private Integer stage;
}
