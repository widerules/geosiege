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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.geosiege.game.resources.GameResources;
import com.zeddic.game.common.GameObject;

public class BackgroundLayer extends GameObject {

  Bitmap bitmap;
  Rect bitmapSrc;
  Rect bitmapDest;
  Paint paint;

  public BackgroundLayer() {
    
    bitmap = GameResources.background;
    bitmapSrc = new Rect(0, 0, GameState.screenWidth, GameState.screenHeight);
    bitmapDest = new Rect(0, 0, GameState.screenWidth, GameState.screenHeight);
  }
  
  public void draw(Canvas c) {
    c.save();
    c.drawBitmap(bitmap, bitmapSrc, bitmapDest, paint);
    c.restore();
  }
  
  public void update(long time) {

  }
}
