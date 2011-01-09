package com.geosiege.game.storage;

import android.app.Activity;

import com.geosiege.game.highscore.HighScores;
import com.geosiege.game.upgrade.Upgrades;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GameStorage {

  private static final String ANALYTICS_ID = "UA-18445489-1";
  private static final int DISPATCH_INTERVAL_SECONDS = 30;
  
  public static GeoStatsRecorder stats;
  public static Preferences preferences;
  public static Upgrades upgrades;
  public static HighScores scores;
  public static GoogleAnalyticsTracker analytics;
  
  private static boolean loaded = false;
  private static int loadCount = 0;
  
  public static void load(Activity activity) {
    
    loadCount++;
    
    // Preferences have to be loaded every activity, since they may be changed
    // outside of the scope of the applications control.
    preferences = new Preferences(activity);
    
    if (loaded) {
      return;
    }
    
    stats =  new GeoStatsRecorder(activity);
    upgrades = new Upgrades(activity);
    scores = new HighScores(activity);
    analytics = GoogleAnalyticsTracker.getInstance();
    analytics.start(ANALYTICS_ID, DISPATCH_INTERVAL_SECONDS, activity);

    loaded = true;
  }
  
  public static void save() {
    if (!loaded) {
      return;
    }
    
    loadCount--;
    if (loadCount > 0) {
      return;
    }
    
    analytics.dispatch();
    analytics.stop();
    
    stats.save();
    upgrades.save();
    scores.save();
   
    // Preferences are read-only in the context of a game. They can only be
    // edited from the Preferences activity.
   
    loaded = false;
  }
}
