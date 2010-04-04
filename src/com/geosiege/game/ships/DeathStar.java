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

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.collision.CollisionComponent;
import com.geosiege.common.collision.CollisionManager;
import com.geosiege.common.explosion.ExplosionManager;
import com.geosiege.common.util.Bounds;
import com.geosiege.common.util.Circle;
import com.geosiege.common.util.Polygon;
import com.geosiege.common.util.Vector2d;
import com.geosiege.common.util.Polygon.PolygonBuilder;
import com.geosiege.game.SimplePathComponent;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Bullet;
import com.geosiege.game.guns.Gun;
import com.geosiege.game.guns.control.AimingGunControl;

public class DeathStar extends EnemyShip {

  
  ////SETUP OBJECT SHAPE AND PAINT
  private static Paint commonPaint;
  private static Polygon commonPolygon;
  private static float ANGLE_OFFSET = -90;
  static {
    commonPaint = new Paint();
    commonPaint.setColor(Color.argb(255, 227, 20, 220));
    commonPaint.setStyle(Paint.Style.STROKE);
    commonPaint.setStrokeWidth(2);
    
    commonPolygon = new PolygonBuilder()
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
  
  
  public Gun gun;
  
  public DeathStar() {
    this(0, 0);
    this.exp = 50;
  }
  
  public DeathStar(float x, float y) {
    super(x, y);
    
    this.setScale(2);
    this.rotation = 20f;
    
    paint = commonPaint;
    bounds = new Bounds(new Circle(10));
    
    gun = Arsenal.getDeathBlossom(this);
    gun.autoFire = true;
    //gun.bulletSpeed = 30;
    //gun.fireCooldown = 2500;

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
  protected float getAngleOffset() {
    return ANGLE_OFFSET;
  }
  
  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    
    canvas.save();

    canvas.translate(x, y);
    canvas.rotate(getAngleOffset() + angle);
    canvas.scale(scale, scale);
    
    canvas.drawPath(commonPolygon.path, paint);
    canvas.restore();
    
    // super.drawBounds(canvas);
  }
  
  /*@Override
  public void collide(PhysicalObject object, Vector2d avoidVector) {
    super.collide(object, avoidVector);
    
    if (object instanceof Bullet) {
      this.active = false;
      ExplosionManager.get().explode(x, y);
    }
  } */

}
