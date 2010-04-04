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
import android.graphics.Rect;

import com.geosiege.common.animation.Transition;
import com.geosiege.common.animation.Transitions;

public class RotatingBackgroundLayer extends BackgroundLayer {

  float rotation;
  
  Transition zoom;
  Transition rotate;
  
  public RotatingBackgroundLayer() {
    super();
    
    int width = (int) (GameState.screenWidth * 1.5f);
    int height = (int) (GameState.screenWidth * 1.5f); 
    
    bitmapSrc = new Rect(0, 0, width, height);
    
    zoom = new Transition(.8f, 1.1f, 40000, Transitions.EASE_IN_OUT);
    zoom.setAutoReverse(true);
    rotate = new Transition(0f, 360f, 120000, Transitions.LINEAR);
    rotate.setAutoReset(true);
    
    bitmapDest = new Rect(
        -width / 2, -height / 2, 
        width / 2, height / 2);
    rotation = 0;
  }
  
  public void draw(Canvas c) {
    c.save();
    c.translate(GameState.screenWidth / 2, GameState.screenHeight / 2);
    c.rotate(rotate.get());
    c.scale(zoom.get(), zoom.get());
    c.drawBitmap(bitmap, bitmapSrc, bitmapDest, paint);
    c.restore();
  }
  
  public void update(long time) {

    zoom.update(time);
    rotate.update(time);
  }
}
