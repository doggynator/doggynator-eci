/* (C) Doggynator 2022 */
package com.doggynator.api.model;

import java.time.Instant;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "REFRESH_TOKEN")
@Table(
    name = "REFRESH_TOKEN",
    uniqueConstraints = {@UniqueConstraint(columnNames = "TOKEN")})
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "REFRESH_TOKEN_ID")
  private long id;

  @OneToOne
  @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
  @NotNull private AppUser user;

  @Column(name = "TOKEN")
  @NotBlank
  private String token;

  @Column(name = "EXPIRATION_DATE")
  @NotNull private Instant expirationDate;
}
