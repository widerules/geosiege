package com.geosiege.game.menu;

import java.io.IOException;

import android.os.Bundle;

import com.geosiege.game.core.GameState;
import com.geosiege.game.level.Level;
import com.geosiege.game.level.LevelLoader;
import com.geosiege.game.menu.Levels.Difficulty;

public class MenuLevel {
 
  private static final String SELECTED_LEVEL_BUNDLE_KEY = "level";
  
  public String file;
  public Difficulty difficulty;
  public boolean locked;
  public Level level;
  
  public MenuLevel(String file, Difficulty difficulty, boolean locked) {
    this.file = file;
    this.locked = locked;
    this.difficulty = difficulty;
    this.loadMetadata();
  }
  
  private void loadMetadata() {
    LevelLoader loader = new LevelLoader(GameState.scores, null);
    try {
      level = loader.loadLevel("levels/" + file, true);
    } catch (IOException e) {
      throw new RuntimeException("Could not load level metadata for " + file, e);
    }
  }
  
  public int getLevelIconResource() {
    return Levels.getDifficultyIcon(difficulty);
  }
  
  public void storeInBundle(Bundle bundle) {
    bundle.putString(SELECTED_LEVEL_BUNDLE_KEY, file);
  }
  
  public static final MenuLevel getLevelFromBundle(Bundle bundle) {
    if (!bundle.containsKey(SELECTED_LEVEL_BUNDLE_KEY)) {
      throw new RuntimeException("Bundle didn't contain level information.");
    }
    
    return Levels.getLevelByFileName(bundle.getString(SELECTED_LEVEL_BUNDLE_KEY));
  }
}
