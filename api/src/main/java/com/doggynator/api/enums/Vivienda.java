/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum Vivienda {
  CASA("0"),
  APARTAMENTO("1");

  String vivienda;

  Vivienda(String vivienda) {
    this.vivienda = vivienda;
  }

  public String getVivienda() {
    return vivienda;
  }
}
