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

package com.geosiege.game.guns;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.collision.CollisionComponent;
import com.geosiege.common.collision.CollisionManager;
import com.geosiege.common.explosion.ExplosionManager;
import com.geosiege.common.util.Bounds;
import com.geosiege.common.util.Circle;
import com.geosiege.common.util.Shape;
import com.geosiege.common.util.Vector2d;
import com.geosiege.game.MapBoundsComponent;


public class Bullet extends PhysicalObject {

  private static final long DEFAULT_MAX_LIFE = 4000;
  
  ////SETUP OBJECT SHAPE AND PAINT
  private static Paint commonPaint;
  private static Shape commonShape;
  private static float ANGLE_OFFSET = 0;
  static {
    commonPaint = new Paint();
    commonPaint.setColor(Color.YELLOW);
    commonPaint.setStyle(Paint.Style.FILL);
    commonPaint.setStrokeWidth(4);
    
    commonShape = new Circle(4);
  }

  public long life; 
  public long maxLife;
  public boolean firedByEnemy = false;
  
  MapBoundsComponent boundsCheck; 
  
  public Bullet(float x, float y) {
    super(x, y);
    
    paint = commonPaint;
    bounds = new Bounds(commonShape);
    life = 0;
    maxLife = DEFAULT_MAX_LIFE;
    
    addComponent(new CollisionComponent(this, CollisionManager.TYPE_HIT_ONLY));

    boundsCheck = new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE);
  }
  
  public void reset() {
    life = 0;
    active = true;
  }
  
  public void draw(Canvas canvas) {
    super.draw(canvas);
    
    
  
    //canvas.save();
    //canvas.translate(x, y);
    //canvas.rotate((ANGLE_OFFSET + angle));
    canvas.drawLine(x, y, x + -velocity.x / 6, y + -velocity.y / 6, paint);
    //canvas.drawCircle(0, 0, bounds.shape.radius, paint);
    //canvas.restore();
  }
  
  public void update(long time) {
    super.update(time);
    life += time;
    if (life > maxLife) {
      this.active = false;
    }
    
    boundsCheck.update(time);
  }
  
  public void offset(float distance) {
    x = x + distance * (float) Math.cos(Math.toRadians(angle));
    y = y + distance * (float) Math.sin(Math.toRadians(angle));
  }
  
  @Override
  protected float getAngleOffset() {
    return ANGLE_OFFSET;
  }
  
  @Override
  public void collide(PhysicalObject other, Vector2d avoidVector) {
    super.collide(other, avoidVector);
    
    ExplosionManager.get().hit(x, y, avoidVector);
    
    this.active = false;
  }
}
