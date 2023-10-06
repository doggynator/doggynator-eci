/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum Tamano {
  PEQUENO("pequeno"),
  MEDIANO("mediano"),
  INTERMEDIO("intermedio"),
  GRANDE("grande");

  String tamano;

  Tamano(String tamano) {
    this.tamano = tamano;
  }

  public String getTamano() {
    return tamano;
  }
}
