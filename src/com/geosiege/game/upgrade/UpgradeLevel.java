package com.geosiege.game.upgrade;

public class UpgradeLevel {
  
  private int cost;
  private float value;
  
  public UpgradeLevel(int cost, float value) {
    this.cost = cost;
    this.value = value;
  }
  
  public int getCost() {
    return cost;
  }
  
  public float getValue() {
    return value;
  }
}
