/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum Trabajo {
  WFH("0"),
  OFICINA("1");

  String trabajo;

  Trabajo(String trabajo) {
    this.trabajo = trabajo;
  }

  public String getTrabajo() {
    return trabajo;
  }
}
