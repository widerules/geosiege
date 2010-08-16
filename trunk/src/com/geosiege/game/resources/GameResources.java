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

package com.geosiege.game.resources;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.SoundPool;

import com.geosiege.common.util.ResourceLoader;
import com.geosiege.game.R;

public class GameResources {
  
  public static Bitmap background;
  public static Bitmap controls;
  public static Typeface font;
  
  private static SoundPool soundPool;
  private static final int SOUND_POOL_SIZE = 40;
  private static final String SPACE_FONT_PATH = "fonts/spaceage.ttf";
  
  public static int SOUND_GUN_1;
  public static int SOUND_GUN_2;
  public static int SOUND_GUN_3;
  public static int SOUND_GUN_4;
  
  public static void load() { 
    background = ResourceLoader.loadBitmap(R.drawable.bg, ResourceLoader.FAST_BITMAP_CONFIG);
    controls = ResourceLoader.loadBitmap(R.drawable.controls, ResourceLoader.FAST_BITMAP_CONFIG);
    font = ResourceLoader.loadFont(SPACE_FONT_PATH);
    
    soundPool = ResourceLoader.createSoundPool(SOUND_POOL_SIZE);
    
    SOUND_GUN_1 = ResourceLoader.loadSound(soundPool, R.raw.laser_01);
    SOUND_GUN_2 = ResourceLoader.loadSound(soundPool, R.raw.gun_02);
    SOUND_GUN_3 = ResourceLoader.loadSound(soundPool, R.raw.gun_03);
    SOUND_GUN_4 = ResourceLoader.loadSound(soundPool, R.raw.gun_04);
  }
  
  public static void play(int id) {
    ResourceLoader.playSound(soundPool, id, 0, 2f);
  }
}
