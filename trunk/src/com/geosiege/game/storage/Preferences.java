package com.geosiege.game.storage;

import android.app.Activity;
import android.content.SharedPreferences;


/**
 * Provides read-only access to preferences set from the preferences activity.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class Preferences {

  public static final String PREFERENCES_FILE_NAME = "GeoSiege.Prefs";
  
  public static final String JOYSTICK_LOCATION_TOPLEFT_BOTTOMRIGHT = "1";
  public static final String JOYSTICK_LOCATION_BOTTOMLEFT_TOPRIGHT = "2";
  public static final String JOYSTICK_LOCATION_BOTH_BOTTOM = "3";
  
  private final SharedPreferences prefs;
  private String joystickLocation;
  private boolean swapMoveAndFireJoysticks;
  private boolean showFps;
  private boolean debugMode;
  private boolean playMusic;
  
  public Preferences(Activity activity) {
    this(activity.getSharedPreferences(PREFERENCES_FILE_NAME, 0));
  }
  
  public Preferences(SharedPreferences prefs) {
    this.prefs = prefs;
    load();
  }
  
  private void load() {
    joystickLocation = prefs.getString("inputLocation", JOYSTICK_LOCATION_TOPLEFT_BOTTOMRIGHT);
    swapMoveAndFireJoysticks = prefs.getBoolean("swapJoysticks", false);
    showFps = prefs.getBoolean("showFps", false);
    debugMode = prefs.getBoolean("debugMode", false);
    playMusic = prefs.getBoolean("playMusic", true);
  }
  
  public String getJoystickLocation() {
    return joystickLocation;
  }
  
  public boolean getSwapJoysticks() {
    return swapMoveAndFireJoysticks;
  }
  
  public boolean getShowFps() {
    return showFps;
  }
  
  public boolean getDebugMode() {
    return debugMode;
  }
  
  public boolean getPlayMusic() {
    return playMusic;
  }
}
