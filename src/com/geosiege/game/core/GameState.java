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

import com.geosiege.common.collision.CollisionManager;
import com.geosiege.common.explosion.ExplosionManager;
import com.geosiege.common.util.ResourceLoader;
import com.geosiege.game.ships.PlayerShip;

public class GameState {
  public static int screenWidth = 800;
  public static int screenHeight = 433;
  public static Vibrator vibrator = null;
  public static Player player = null;
  public static PlayerShip playerShip = null;
  public static Map map = null;
  public static Camera camera = null;
  public static ExplosionManager explosionManager = null;
  public static Context context = null;
  public static Activity activity;
  
  public static void setup(Activity activity) {
    GameState.activity = activity;
    GameState.context = activity.getApplicationContext();
    ResourceLoader.init(context);
    GameState.vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
  }
  
  public static void setScreen(int width, int height) {
    // Force horizontal mode. Android does not appear to be deterministic
    // in how it reports screen width / height.
    screenWidth = Math.max(width, height);
    screenHeight = Math.min(width, height);
  }
}
