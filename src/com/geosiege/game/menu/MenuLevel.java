package com.geosiege.game.menu;

import android.os.Bundle;

import com.geosiege.game.menu.Levels.Difficulty;

public class MenuLevel {
 
  private static final String SELECTED_LEVEL_BUNDLE_KEY = "level";
  
  public String file;
  public String name;
  public Difficulty difficulty;
  public boolean locked;
  
  public MenuLevel(String file, String name, Difficulty difficulty, boolean locked) {
    this.file = file;
    this.name = name;
    this.locked = locked;
    this.difficulty = difficulty;
  }
  
  public int getLevelIconResource() {
    return Levels.getDifficultyIcon(difficulty);
  }
  
  public void storeInBundle(Bundle bundle) {
    bundle.putString(SELECTED_LEVEL_BUNDLE_KEY, name);
  }
  
  public static final MenuLevel getLevelFromBundle(Bundle bundle) {
    if (!bundle.containsKey(SELECTED_LEVEL_BUNDLE_KEY)) {
      throw new RuntimeException("Bundle didn't contain level information.");
    }
    
    return Levels.getLevelWithName(bundle.getString(SELECTED_LEVEL_BUNDLE_KEY));
  }
}
