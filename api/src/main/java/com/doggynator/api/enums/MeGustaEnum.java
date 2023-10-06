/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum MeGustaEnum {
  GUSTA(5),
  DISGUSTA(1);
  int megusta;

  MeGustaEnum(int megusta) {
    this.megusta = megusta;
  }

  public int getMeGusta() {
    return megusta;
  }
}
