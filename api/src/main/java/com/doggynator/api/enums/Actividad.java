/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum Actividad {
  NETFLIX("0"),
  CINE("1");
  String actividad;

  Actividad(String actividad) {
    this.actividad = actividad;
  }

  public String getActividad() {
    return actividad;
  }
}
