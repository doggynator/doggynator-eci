/* (C) Doggynator 2022 */
package com.doggynator.api.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
public class Perro implements Serializable {
  public Perro(Perro perro) {
    this.perroid = perro.perroid;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long perroid;

  Integer cluster;
  String nombre;
  float edad;
  String raza;
  String tamano;
  String sexo;
  String color;
  String pelaje;
  String fundacion;
  Integer atencionesespeciales;
  String necesidades;
  Integer ninos;
  Integer entrenado;
  Integer esterilizado;
  Integer otrosperros;
  Integer actividadfisica;
  String agresividad;
}
