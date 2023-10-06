/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum Salir {
  INDOOR("0"),
  OUTDOOR("1");

  String salir;

  Salir(String salir) {
    this.salir = salir;
  }

  public String getSalir() {
    return salir;
  }
}
