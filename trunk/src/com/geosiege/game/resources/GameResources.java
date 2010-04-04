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

import com.geosiege.common.util.ResourceLoader;
import com.geosiege.game.R;

public class GameResources {
  
  public static Bitmap background;
  public static Bitmap controls;
  public static Typeface font;
  
  static { 
    background = ResourceLoader.loadBitmap(R.drawable.bg, ResourceLoader.FAST_BITMAP_CONFIG);
    controls = ResourceLoader.loadBitmap(R.drawable.controls, ResourceLoader.FAST_BITMAP_CONFIG);
    font = ResourceLoader.loadFont("fonts/spaceage.ttf");
  }
}
