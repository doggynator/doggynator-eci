/* (C) Doggynator 2023 */
package com.doggynator.api.enums;

public enum USettingsStage {
  FORM(0),
  LIKES(1),
  RECOMMENDER(2);

  Integer stage;

  USettingsStage(Integer stage) {
    this.stage = stage;
  }

  public Integer getStage() {
    return this.stage;
  }
}
