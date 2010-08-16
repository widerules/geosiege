package com.geosiege.game.menu;

import java.util.List;

public class MenuLevelGroup {
  
  public String name;
  public List<MenuLevel> levels;
  
  public MenuLevelGroup(String name, List<MenuLevel> levels) {
    this.name = name;
    this.levels = levels;
  }
}
