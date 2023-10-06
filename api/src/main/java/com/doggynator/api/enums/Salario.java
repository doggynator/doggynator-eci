/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

import java.util.List;

public enum Salario {
  /*
   * Ingresos < 1.000.000
   */
  Salario0("a", "0 -1.000.000"),
  /*
   * Ingresos 1.000.000 - 5.000.000
   */
  Salario1("b", "1.000.001 - 5.000.000"),
  /*
   * Ingresos > 5.000.000
   */
  Salario2("c", "mayor a 5.000.000");

  String salario;
  String salarioReq;

  Salario(String salario, String salarioReq) {
    this.salario = salario;
    this.salarioReq = salarioReq;
  }

  public String getSalario() {
    return salario;
  }

  public String getSalarioReq() {
    return salarioReq;
  }

  public static Salario fromSalarioReq(final String salarioReq) {
    return List.of(Salario.values()).stream()
        .filter(s -> s.getSalarioReq().equals(salarioReq))
        .findFirst()
        .orElse(null);
  }
}
