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

import android.graphics.Canvas;

public class ScrollingBackgroundLayer extends BackgroundLayer {

  private Map map;

  public ScrollingBackgroundLayer(Map map) {
    this.map = map;
  }
  
  public void draw(Canvas c) {
   
    Camera camera = Camera.camera;
    
    float maxBitmapX = Math.min(bitmap.getWidth() - camera.width, map.width * .1f);
    float maxBitmapY = Math.min(bitmap.getHeight() - camera.height, map.height * .1f);
    
    int x = (int)((camera.right - map.left) * maxBitmapX / map.width);
    int y = (int)((camera.top - map.top) * maxBitmapY / map.height);
    
    bitmapDest.left = 0;
    bitmapDest.right = (int) camera.width;
    bitmapDest.top = 0;
    bitmapDest.bottom = (int) camera.height;
    
    bitmapSrc.left = x;
    bitmapSrc.right = x + (int) camera.width;
    bitmapSrc.top = y;
    bitmapSrc.bottom = y + (int) camera.height;
    
    c.save();
    c.translate(camera.left, camera.top);
    c.drawBitmap(bitmap, bitmapSrc, bitmapDest, paint);
    c.restore();
  }
}
