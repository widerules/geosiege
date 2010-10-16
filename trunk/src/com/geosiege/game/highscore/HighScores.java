package com.geosiege.game.highscore;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.geosiege.game.core.GameState;

/**
 * Provides read-only access to preferences set from the preferences activity.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class HighScores {

  private static final String PREFERENCES_FILE_NAME = "GeoSiege.HiScores";
  private static final String LEVEL_PREFIX = "LEVEL_";
  private int DEFAULT_HIGHSCORE = 10;
  
  private SharedPreferences prefs;
  private Editor editor;
  
  
  public HighScores() {
    this(GameState.activity.getSharedPreferences(PREFERENCES_FILE_NAME, 0));
  }
  
  public HighScores(SharedPreferences prefs) {
    this.prefs = prefs;
    this.editor = prefs.edit();
  }
  
  /*private void load() {
    joystickLocation = prefs.getString("inputLocation", JOYSTICK_LOCATION_TOPLEFT_BOTTOMRIGHT);
    swapMoveAndFireJoysticks = prefs.getBoolean("swapJoysticks", false);
    showFps = prefs.getBoolean("showFps", false);
    debugMode = prefs.getBoolean("debugMode", false);
  }*/
  
  public boolean containsScore(int levelId) {
    return prefs.contains(LEVEL_PREFIX + levelId);
  }
  
  public int getHighScore(int levelId) {
    return prefs.getInt(LEVEL_PREFIX + levelId, DEFAULT_HIGHSCORE);
  }
  
  public void setHighScore(int levelId, int score) {
    editor.putInt(LEVEL_PREFIX + levelId, score);
    editor.commit();    
  }
}
