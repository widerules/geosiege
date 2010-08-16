package com.geosiege.game;

public class MenuStat {

  public String name;
  public String value;

  public MenuStat(String name, String value) {
    this.name = name;
    this.value = value;
  }
  
  public MenuStat(String name, int value) {
    this.name = name;
    this.value = Integer.toString(value);
  }
}
