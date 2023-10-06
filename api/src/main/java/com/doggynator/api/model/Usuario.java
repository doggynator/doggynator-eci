/* (C) Doggynator 2022 */
package com.doggynator.api.model;

import java.io.Serializable;

import javax.persistence.Column;
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
public class Usuario implements Serializable {

  public Usuario(Usuario usuario) {
    this.userid = usuario.userid;
  }

  @Id private Long userid;

  private Integer cluster;
  private Integer viajes;
  private Integer ciclovia;
  private Integer locha;
  private Integer disciplina;
  private Integer silencio;

  @Column(name = "tipofamilia")
  private String tipoFamilia;

  private String actividad;
  private String salir;
  private String estilo;
  private String vivienda;
  private String estrato;
  private String sueldo;
  private Integer amigos;
  private String trabajo;
  private Integer fpy;
  private Integer edad;
}
