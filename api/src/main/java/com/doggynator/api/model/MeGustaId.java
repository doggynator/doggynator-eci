/* (C) Doggynator 2022 */
package com.doggynator.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MeGustaId implements Serializable {
  @Column(name = "userid")
  Long userid;

  @Column(name = "perroid")
  Long perroid;
}
