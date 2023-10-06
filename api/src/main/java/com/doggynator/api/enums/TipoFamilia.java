/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum TipoFamilia {
  NOHIJOS("sin hijos", "1"),
  HIJOS("hijos", "0");

  String tipoFamilia;
  String tipoFamiliaReq;

  TipoFamilia(String tipoFamilia, String tipoFamiliaReq) {
    this.tipoFamilia = tipoFamilia;
    this.tipoFamiliaReq = tipoFamiliaReq;
  }

  public String getTipoFamilia() {
    return tipoFamilia;
  }

  public String getTipoFamiliaReq() {
    return tipoFamiliaReq;
  }
}
