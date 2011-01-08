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
import com.geosiege.game.level.Level;
import com.geosiege.game.level.Stockpiles;
import com.geosiege.game.resources.GameResources;
import com.zeddic.game.common.effects.Effects;
import com.zeddic.game.common.util.ResourceLoader;

public class GameState {

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
  
  private static boolean loaded = false;
  private static int loadCount = 0;
  
  public static void setup(Activity activity) {
    
    GameState.activity = activity;
    GameState.context = activity;
    
    loadCount++;
    
    if (loaded) {
      return;
    }

    ResourceLoader.init(context);
    GameResources.load();
    GameState.vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

    loaded = true;
  }
  
  public static void cleanup() {
    
    activity = null;
    context = null;
    
    if (!loaded) {
      return;
    }
    
    loadCount--;
    if (loadCount > 0) {
      return;
    }
    
    GameResources.cleanup();
    
    // Remove all references to globals so they can be garbage collected.
    vibrator = null;
    player = null;
    stockpiles = null;
    camera = null;
    effects = null;
    geoEffects = null;
    context = null;
    level = null;
    loaded = false;
  }
  
  public static void setScreen(int width, int height) {
    // Force horizontal mode. Android does not appear to be deterministic
    // in how it reports screen width / height.
    screenWidth = Math.max(width, height);
    screenHeight = Math.min(width, height);
  }
}
