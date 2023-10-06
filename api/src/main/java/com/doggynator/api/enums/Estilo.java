/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum Estilo {
  ORDEN("orden", "0"),
  DESORDEN("desorden", "1");

  String estilo;
  String estiloReq;

  Estilo(String estilo, String estiloReq) {
    this.estilo = estilo;
    this.estiloReq = estiloReq;
  }

  public String getEstilo() {
    return estilo;
  }

  public String getEstiloReq() {
    return estiloReq;
  }
}
