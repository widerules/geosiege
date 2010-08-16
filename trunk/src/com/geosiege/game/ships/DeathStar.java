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

package com.geosiege.game.ships;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.geosiege.common.collision.CollisionComponent;
import com.geosiege.common.collision.CollisionManager;
import com.geosiege.common.util.Bounds;
import com.geosiege.common.util.Circle;
import com.geosiege.common.util.ComponentManager;
import com.geosiege.common.util.Polygon;
import com.geosiege.common.util.Polygon.PolygonBuilder;
import com.geosiege.game.components.MapBoundsComponent;
import com.geosiege.game.components.SimplePathComponent;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Gun;

public class DeathStar extends EnemyShip {

  
  ////SETUP OBJECT SHAPE AND PAINT
  private static final Paint PAINT;
  private static final Polygon SHAPE;
  private static final float ANGLE_OFFSET = -90;
  static {
    PAINT = new Paint();
    PAINT.setColor(Color.argb(255, 227, 20, 220));
    PAINT.setStyle(Paint.Style.STROKE);
    PAINT.setStrokeWidth(2);
    
    SHAPE = new PolygonBuilder()
        .add(-3, -3)
        .add(0, -9)
        .add(3, -3)
        .add(8, -2)
        .add(4, 2)
        .add(5, 8)
        .add(0, 5)
        .add(-5, 8)
        .add(-4, 2)
        .add(-8, -2)
        .build();
  }
  
  private ComponentManager components;
  private Gun gun;
  
  public DeathStar() {
    this(0, 0);
    this.exp = 50;
  }
  
  public DeathStar(float x, float y) {
    super(x, y);
    
    this.paint = PAINT;
    this.setScale(2);
    this.rotation = 20f;
    this.bounds = new Bounds(new Circle(10));
    
    gun = Arsenal.getDeathBlossom(this);
    gun.setAutoFire(true);
    
    components = new ComponentManager(this);
    components.add(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    components.add(new SimplePathComponent(this, GameState.playerShip, 100));
    components.add(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE));
    components.add(gun); 
  }
  
  @Override
  public void reset() {
    super.reset();
    gun.reset();
  }

  @Override
  protected float getAngleOffset() {
    return ANGLE_OFFSET;
  }
  
  @Override
  public void update(long time) {
    super.update(time);
    if (!isSpawning())
      components.update(time);
  }
  
  @Override
  public void draw(Canvas canvas) {
    
    super.draw(canvas);
    
    canvas.save();
    canvas.translate(x, y);
    canvas.rotate(getAngleOffset() + angle);
    canvas.scale(scale, scale);
    canvas.drawPath(SHAPE.path, paint);
    canvas.restore();
    
    components.draw(canvas);
  }
}
