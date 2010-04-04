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

package com.geosiege.common.particle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.geosiege.common.GameObject;
import com.geosiege.common.PhysicalObject;
import com.geosiege.common.util.Vector2d;

public class Particle extends GameObject {

  public float x;
  public float y;
  public Vector2d velocity; 
  public float scale;
  public float maxLife;
  public float life;
  public float acceleration;
  public float alpha;
  public float alphaRate;
  
  Paint paint;
  Vector2d scaledVelocity = new Vector2d(0, 0);
  
  public Particle() {
    this(0, 0);
  }
  
  public Particle(float x, float y) {
    this.x = x;
    this.y = y;
    this.velocity = new Vector2d(0, 0);
    this.scale = 3;
    this.life = 0;
    
    paint = new Paint();
    paint.setColor(Color.RED);
    paint.setStrokeWidth(4);
  }

  public void setVelocity(float x, float y) {
    velocity.x = x;
    velocity.y = y;
  }
  
  public void setVelocityBySpeed(float angle, float speed) {
    double radians = Math.toRadians(angle);
    velocity.x = speed * (float) Math.cos(radians);
    velocity.y = speed * (float) Math.sin(radians);
  }
  
  float timeFraction;
  public void update(long time) {
    super.update(time);
    
    life += time;
    if (life > maxLife) {
      this.active = false;
    }

    timeFraction = (float) time / PhysicalObject.TIME_SCALER;
    x += velocity.x * timeFraction;
    y += velocity.y * timeFraction;
    
    scaledVelocity.x = velocity.x * timeFraction;
    scaledVelocity.y = velocity.y * timeFraction;
    
  }
  
  public void draw(Canvas c) {
    paint.setAlpha((int) alpha);
    alpha += alphaRate;
    c.drawLine(x, y, x + -scaledVelocity.x, y + -scaledVelocity.y, paint);
    //c.drawCircle(x, y, scale, paint);
  }
}
