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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

import com.zeddic.game.common.GameObject;

public class Map extends GameObject {
  
  public static Map map = null;
  
  private static final float EDGE_BUFFER = 100;
  private static final float SPAWN_BUFFER = 50;
  private static final float DEFAULT_WIDTH = 900;
  private static final float DEFAULT_HEIGHT = 600;
  ScrollingBackgroundLayer background;
  
  public float width;
  public float height;
  public float top;
  public float left;
  public float right;
  public float bottom;
  
  public float spawnLeft;
  public float spawnTop;
  public float spawnWidth;
  public float spawnHeight;
  public float spawnRight;
  public float spawnBottom;

  Path borderPath; 
  Paint paint;
  
  public Map() {
    paint = new Paint();
    paint.setStyle(Style.STROKE);
    paint.setStrokeWidth(3);
    paint.setStrokeJoin(Join.MITER);
    paint.setColor(Color.WHITE);

    map = this;
    
    background = new ScrollingBackgroundLayer(this);
    
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }
  
  public void setSize(float width, float height) {
    this.width = width;
    this.height = height;
    top = 0 + EDGE_BUFFER;
    left = 0 + EDGE_BUFFER;
    right = left + width; 
    bottom = top + height;
    
    spawnLeft = left + SPAWN_BUFFER;
    spawnTop = top + SPAWN_BUFFER;
    spawnWidth = width - 2 * SPAWN_BUFFER;
    spawnHeight = height - 2 * SPAWN_BUFFER;
    spawnBottom = spawnTop + spawnHeight;
    spawnRight = spawnLeft + spawnWidth;
    
    borderPath = new Path();
    borderPath.moveTo(left, top);
    borderPath.lineTo(right, top);
    borderPath.lineTo(right, bottom);
    borderPath.lineTo(left, bottom);
    borderPath.lineTo(left, top);
  }
  
  public boolean inSpawnableArea(float x, float y) {
    return x >= spawnLeft && x <= spawnLeft + spawnWidth &&
        y >= spawnTop && y <= spawnTop + spawnHeight;
  }
  
  public void update(long time) {
    background.update(time);
  }
  
  public void draw(Canvas c) {
    background.draw(c);
    c.drawPath(borderPath, paint);
  }
}
