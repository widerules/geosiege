package com.geosiege.game.stats;

import android.content.SharedPreferences;

import com.geosiege.game.core.GameState;

public class GeoStatsRecorder {

  public static final String PREFERENCES_FILE_NAME = "GeoSiege.Stats";
  private static final int SECONDS_IN_DAY = 60 * 60 * 24;
  private static final int SECONDS_IN_HOUR = 60 * 60;
  private static final int SECONDS_IN_MINUTE = 60;
  
  private static final String KEY_KILL_COUNT = "KillCount";
  private static final String KEY_TIME_PLAYED = "TimePlayed";
  
  private SharedPreferences prefs;
  private SharedPreferences.Editor editor;
  
  private int enemiesKilled  = 0;
  private long timePlayed = 0;
  
  public GeoStatsRecorder() {
    this(GameState.activity.getSharedPreferences(PREFERENCES_FILE_NAME, 0));
  }
  
  public GeoStatsRecorder(SharedPreferences prefs) {  
    this.prefs = prefs;
    this.editor = prefs.edit();
    load();
  }
  
  public void recordKill() {
    enemiesKilled++;
  }
  
  public void recordTimePlayed(long time) {
    timePlayed += time;
  }
  
  public int getNumberOfEnemeiesKilled() {
    return enemiesKilled;
  }
  
  public long getTimePlayed() {
    return timePlayed / 1000;
  }
  
  public String getTimePlayedAsString() {
    long totalSeconds = timePlayed / 1000;
    
    int days = (int) (totalSeconds / SECONDS_IN_DAY);
    totalSeconds = totalSeconds % SECONDS_IN_DAY;
    
    int hours = (int) (totalSeconds / SECONDS_IN_HOUR);
    totalSeconds = totalSeconds % SECONDS_IN_HOUR;
    
    int minutes = (int) (totalSeconds / SECONDS_IN_MINUTE);
    totalSeconds = totalSeconds % SECONDS_IN_MINUTE;
    
    return convertDurationToString(days, hours, minutes, (int) totalSeconds);
  }
  
  public void load() {
    enemiesKilled = prefs.getInt(KEY_KILL_COUNT, 0);
    timePlayed = prefs.getLong(KEY_TIME_PLAYED, 0);
  }
  
  public void save() {
    editor.putInt(KEY_KILL_COUNT, enemiesKilled);
    editor.putLong(KEY_TIME_PLAYED, timePlayed);
    editor.commit();
  }
  
  private static String convertDurationToString(int days, int hours, int minutes, int seconds) {
    StringBuilder duration = new StringBuilder();
    if (days == 1) {
      duration.append(days + " day");
    } else if (days > 0) {
      duration.append(days + " days");
    }
    if (hours == 1) {
      duration.append(" " + hours + " h");
    } else if (days > 0) {
      duration.append(" " + hours + " h");
    }
    if (minutes > 0) {
      duration.append(" " + minutes + " m");
    }
    
    duration.append(" " + seconds + " s");
    
    return duration.toString();
  }
}
