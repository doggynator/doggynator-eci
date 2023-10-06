/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

import java.util.List;

public enum Estrato {
  ESTRATO1(1, "Estrato 1"),
  ESTRATO2(2, "Estrato 2"),
  ESTRATO3(3, "Estrato 3"),
  ESTRATO4(4, "Estrato 4"),
  ESTRATO5(5, "Estrato 5"),
  ESTRATO6(6, "Estrato 6");

  int estrato;
  String estratoReq;

  private Estrato(int estrato, String estratoReq) {
    this.estrato = estrato;
    this.estratoReq = estratoReq;
  }

  public int getEstrato() {
    return estrato;
  }

  public String getEstratoReq() {
    return estratoReq;
  }

  public static Estrato fromEstratoReq(final String estratoReq) {
    return List.of(Estrato.values()).stream()
        .filter(e -> e.getEstratoReq().equals(estratoReq))
        .findFirst()
        .orElse(null);
  }
}
