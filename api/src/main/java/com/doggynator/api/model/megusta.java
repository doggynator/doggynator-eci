/* (C) Doggynator 2022 */
package com.doggynator.api.model;

import java.sql.Timestamp;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class megusta {

  @EmbeddedId MeGustaId id;

  Double megusta;
  Timestamp rated_date;
}
