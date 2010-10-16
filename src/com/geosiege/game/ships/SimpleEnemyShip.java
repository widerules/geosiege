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

import com.geosiege.game.components.MapBoundsComponent;
import com.geosiege.game.components.SimplePathComponent;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Gun;
import com.geosiege.game.guns.control.AimingGunControl;
import com.zeddic.game.common.collision.CollisionComponent;
import com.zeddic.game.common.collision.CollisionManager;
import com.zeddic.game.common.util.Bounds;
import com.zeddic.game.common.util.Circle;
import com.zeddic.game.common.util.ComponentManager;
import com.zeddic.game.common.util.Polygon;
import com.zeddic.game.common.util.Polygon.PolygonBuilder;

public class SimpleEnemyShip extends EnemyShip {

  //// SETUP OBJECT SHAPE AND PAINT
  private static final Paint PAINT;
  private static final Polygon SHAPE;
  static {
    PAINT = new Paint();
    PAINT.setColor(Color.GREEN);
    PAINT.setStyle(Paint.Style.STROKE);
    PAINT.setStrokeWidth(2);
    
    SHAPE = new PolygonBuilder()
        .add(0, -20)
        .add(10, 0)
        .add(0, 20)
        .add(-10, 0)
        .build();
  }

  private Gun gun;
  private ComponentManager components;
  
  public SimpleEnemyShip() {
    this(0, 0);
    exp = 10;
  }
  
  public SimpleEnemyShip(float x, float y) {
    super(x, y);
    
    paint = PAINT;
    bounds = new Bounds(new Circle(15));
    
    gun = Arsenal.getPeaShooter(this);
    gun.setGunControl(new AimingGunControl(this, GameState.player.ship, 200, 25));
    gun.setAutoFire(true);
    gun.setBulletSpeed(70);
    gun.setFireCooldown(800);
    
    components = new ComponentManager(this);
    components.add(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    components.add(new SimplePathComponent(this, GameState.player.ship, 120, 30, 100));
    components.add(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE));
    components.add(gun);
  }
  
  @Override
  public void reset() {
    super.reset();
    gun.reset();
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
