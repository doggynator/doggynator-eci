/* (C) Doggynator 2022 */
package com.doggynator.api.enums;

public enum Raza {
  PINCHER("pincher"),
  PASTOR_ALEMAN("pastor aleman"),
  BULLDOG("bulldog"),
  BEAGLE("beagle"),
  YORKIE("yorkie"),
  JACK_RUSSEL("jack russel"),
  BORDER_COLLIE("border collie"),
  MALINOIS("malinois"),
  GRAN_DANES("gran danes"),
  BULL_TERRIER("bull terrier"),
  PASTOR_OVEJERO("pastor_ovejero"),
  LABRADOR("labrador"),
  WEST_HIGHLAND("West highland"),
  FRENCH_POODLE("french poodle"),
  DALMATA("dalmata"),
  AKITA("akita"),
  SAN_BERNARDO("san bernardo"),
  HUSKY("husky"),
  SCHNAUZER("schnauzer"),
  SHIH_TZU("shih tzu"),
  SALCHICHA("salchicha"),
  POMERANIA("pomerania"),
  PITBULL("pitbull"),
  ROTTWEILER("rottweiler"),
  CRIOLLO("criollo"),
  TERRANOVA("terranova"),
  PUG("pug");

  String raza;

  Raza(String raza) {
    this.raza = raza;
  }

  public String getRaza() {
    return raza;
  }
}
