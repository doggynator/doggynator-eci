/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum Viajes {
  VIAJA("1"),
  NOVIAJA("0");
  String viaja;

  private Viajes(String viaja) {
    this.viaja = viaja;
  }

  public String getViaja() {
    return viaja;
  }
}
