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
import com.geosiege.common.util.Polygon;
import com.geosiege.common.util.Polygon.PolygonBuilder;
import com.geosiege.game.SimplePathComponent;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Gun;
import com.geosiege.game.guns.control.AimingGunControl;

public class SimpleEnemyShip extends EnemyShip {

  //// SETUP OBJECT SHAPE AND PAINT
  private static Paint commonPaint;
  private static Polygon commonPolygon;
  static {
    commonPaint = new Paint();
    commonPaint.setColor(Color.GREEN);
    commonPaint.setStyle(Paint.Style.STROKE);
    commonPaint.setStrokeWidth(2);
    
    commonPolygon = new PolygonBuilder()
        .add(0, -20)
        .add(10, 0)
        .add(0, 20)
        .add(-10, 0)
        .build();
  }
  

  public Gun gun;
  
  public SimpleEnemyShip() {
    this(0, 0);
    exp = 10;
  }
  
  public SimpleEnemyShip(float x, float y) {
    super(x, y);
    
    this.paint = commonPaint;
    bounds = new Bounds(new Circle(15));
    
    gun = Arsenal.getPeaShooter(this);
    gun.setGunControl(new AimingGunControl(this, PlayerShip.ship, 200, 50));
    gun.setAutoFire(true);
    gun.setBulletSpeed(30);
    gun.setFireCooldown(2500);
    
    addComponent(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    addComponent(new SimplePathComponent(this, PlayerShip.ship, 100));
    addComponent(gun);
  }
  
  @Override
  public void reset() {
    super.reset();
    gun.reset();
  }
  
  
  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    //super.drawBounds(canvas);
    canvas.save();

    canvas.translate(x, y);
    canvas.rotate(getAngleOffset() + angle);
    canvas.scale(scale, scale);
    
    canvas.drawPath(commonPolygon.path, paint);
    canvas.restore();
  }
}
