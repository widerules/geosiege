package com.geosiege.game.highscore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
  
  
  public HighScores(Activity activity) {
    this(activity.getSharedPreferences(PREFERENCES_FILE_NAME, 0));
  }
  
  public HighScores(SharedPreferences prefs) {
    this.prefs = prefs;
    this.editor = prefs.edit();
  }
  
  public void save() {
    editor.commit();
  }
  
  public boolean containsScore(int levelId) {
    return prefs.contains(LEVEL_PREFIX + levelId);
  }
  
  public int getHighScore(int levelId) {
    return prefs.getInt(LEVEL_PREFIX + levelId, DEFAULT_HIGHSCORE);
  }
  
  public void setHighScore(int levelId, int score) {
    editor.putInt(LEVEL_PREFIX + levelId, score);  
  }
}
