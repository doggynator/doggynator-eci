/* (C) Doggynator 2023 */
package com.doggynator.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@ToString
public class RecomenderPojo {
  // dog info
  Long perroid;
  float dogEdad;
  String tamano;
  String sexo;
  String color;
  String pelaje;
  Integer atencionesespeciales;
  Integer ninos;
  Integer entrenado;
  Integer esterilizado;
  Integer perros;
  Integer actividad_y; // actividadfisica;
  String agresividad;
  // user info
  private Long userid;
  private Integer viajes;
  private String tipofamilia;
  private String actividad_x;
  private String salir;
  private String estilo;
  private String vivienda;
  private String estrato;
  private String sueldo;
  private String trabajo;
  private Integer userEdad;
}
