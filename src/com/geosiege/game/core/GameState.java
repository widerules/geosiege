/*
 * Copyright (C) 2010 Geo Siege Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geosiege.game.core;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

import com.geosiege.game.effects.GeoEffects;
import com.geosiege.game.highscore.HighScores;
import com.geosiege.game.level.Level;
import com.geosiege.game.level.Stockpiles;
import com.geosiege.game.resources.GameResources;
import com.geosiege.game.storage.GeoStatsRecorder;
import com.geosiege.game.storage.Preferences;
import com.geosiege.game.upgrade.Upgrades;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.zeddic.game.common.effects.Effects;
import com.zeddic.game.common.util.ResourceLoader;

public class GameState {
  
  private static final String ANALYTICS_ID = "UA-18445489-1";
  private static final int DISPATCH_INTERVAL_SECONDS = 30;
  
  public static int screenWidth = 800;
  public static int screenHeight = 433;
  public static Vibrator vibrator = null;
  public static Player player = null;
  public static Stockpiles stockpiles = null;
  public static Camera camera = null;
  public static Effects effects = null;
  public static GeoEffects geoEffects = null;
  public static Context context = null;
  public static Activity activity;
  public static Level level = null;
  public static GeoStatsRecorder stats = null;
  public static Preferences preferences = null;
  public static Upgrades upgrades = null;
  public static HighScores scores = null;
  public static GoogleAnalyticsTracker analytics = null;
  
  private static boolean resourcesLoaded = false;
  
  public static void setup(Activity activity) {
    
    GameState.activity = activity;
    GameState.context = activity;
    
    if (resourcesLoaded)
      return;
    
    GameState.stats =  new GeoStatsRecorder();
    GameState.preferences = new Preferences();
    GameState.upgrades = new Upgrades();
    GameState.scores = new HighScores();
    GameState.analytics = GoogleAnalyticsTracker.getInstance();
    GameState.analytics.start(ANALYTICS_ID, DISPATCH_INTERVAL_SECONDS, activity);

    ResourceLoader.init(context);
    GameResources.load();
    GameState.vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

    resourcesLoaded = true;
  }
  
  public static void cleanup() {
    if (GameState.analytics != null) {
      GameState.analytics.dispatch();
      GameState.analytics.stop();
    }
    
    if (GameState.stats != null) {
      GameState.stats.save();
    }
    
    if (GameState.upgrades != null) {
      GameState.upgrades.save();
    }
    
    GameResources.cleanup();
    
    resourcesLoaded = false;
  }
  
  public static void setScreen(int width, int height) {
    // Force horizontal mode. Android does not appear to be deterministic
    // in how it reports screen width / height.
    screenWidth = Math.max(width, height);
    screenHeight = Math.min(width, height);
  }
}
