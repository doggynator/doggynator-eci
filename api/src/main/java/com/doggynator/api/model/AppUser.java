/* (C) Doggynator 2022 */
package com.doggynator.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "APP_USER",
    uniqueConstraints = {@UniqueConstraint(columnNames = "EMAIL")})
public class AppUser implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "USER_ID")
  private Long id;

  @NotBlank
  @Size(max = 50)
  @Column(name = "USERNAME")
  private String username;

  @Column(name = "PROVIDER_USER_ID")
  private String providerUserId;

  @NotBlank
  @Size(max = 50)
  @Email
  @Column(name = "EMAIL")
  private String email;

  @Column(name = "ENABLED")
  private boolean enabled;

  @Column(name = "CREATED_DATE", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  protected Date createdDate;

  @Temporal(TemporalType.TIMESTAMP)
  protected Date modifiedDate;

  @NotBlank
  @Size(max = 120)
  private String password;

  private String provider;

  // bi-directional many-to-many association to Role
  @JsonIgnore
  @ManyToMany
  @JoinTable(
      name = "user_role",
      joinColumns = {@JoinColumn(name = "USER_ID")},
      inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")})
  private Set<AppRole> roles = new HashSet<>();
}
