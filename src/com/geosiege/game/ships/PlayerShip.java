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
import android.graphics.Paint.Style;

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.collision.CollisionComponent;
import com.geosiege.common.collision.CollisionManager;
import com.geosiege.common.particle.ParticleEmitter;
import com.geosiege.common.particle.ParticleEmitter.ParticleEmitterBuilder;
import com.geosiege.common.util.Bounds;
import com.geosiege.common.util.Circle;
import com.geosiege.common.util.Polygon;
import com.geosiege.common.util.RandomUtil;
import com.geosiege.common.util.Vector2d;
import com.geosiege.common.util.Polygon.PolygonBuilder;
import com.geosiege.game.MapBoundsComponent;
import com.geosiege.game.core.ArcadeGameMode;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Bullet;
import com.geosiege.game.guns.Gun;

public class PlayerShip extends Ship {
  
  //// SETUP OBJECT SHAPE AND PAINT
  private static Paint commonPaint;
  private static Polygon commonPolygon;
  private static float ANGLE_OFFSET = -90;
  static {
    commonPaint = new Paint();
    commonPaint.setColor(Color.BLUE);
    commonPaint.setStyle(Paint.Style.STROKE);
    commonPaint.setStrokeWidth(3);
    // paint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.OUTER));
    // blurFilter = new BlurMaskFilter(18, BlurMaskFilter.Blur.NORMAL);

    commonPolygon = new PolygonBuilder()
        .add(0, 17)
        .add(12, -8)
        .add(0, -16)
        .add(-12, -8)
        .build();
  }
  
  private static final long TIME_TO_WATCH_DEATH = 0;
  private static final long TIME_BETWEEN_DEATH_EXPLOSIONS = 100;
  
  public ArcadeGameMode gameMode;
  
  ParticleEmitter emitter;
  Gun gun;
  
  public static PlayerShip ship;
  MapBoundsComponent boundsComponent;
  
  boolean dying;
  boolean dead;
  long deadWait;
  long dieExplodeWait;
  
  public PlayerShip(float x, float y) {
    super(x, y);
    
    dying = false;
    dead = false;
    deadWait = 0;
    dieExplodeWait = 0;
    health = GameState.player.maxHealth;
    paint = new Paint(commonPaint);
    setBounds(new Bounds(new Circle(8)));
    
    addComponent(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    boundsComponent = new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE);
    
    emitter = new ParticleEmitterBuilder()
        .at(x, y)
        .withEmitMode(ParticleEmitter.MODE_DIRECTIONAL)
        .withEmitAngleJitter(15)
        .withEmitSpeedJitter(10)
        .withParticleSpeed(25)
        .withParticleAlphaRate(30)
        .withParticleLife(700)
        .withMaxParticles(100)
        .withEmitRate(60)
        .build();
    
    gun = Arsenal.getTriGun(this);
    gun.setFireCooldown(200);
    gun.setBulletSpeed(150);
    gun.setFireOffset(40);
    //gun.fireCooldown = 200;
    //gun.bulletSpeed = 110f;
    //gun.fireOffset = 40;
    //gun.control = new DirectionalGunControl(this, 0);
    
    addComponent(gun);
    
    ship = this;
  }
  
  public void setAngle(float angle) {
    if (!dead) {
      super.setAngle(angle);
    }
  }
  
  public void update(long time) {
    
    if (dying || dead) {

      deadWait += time;
      dieExplodeWait += time;
      
      if (dieExplodeWait > TIME_BETWEEN_DEATH_EXPLOSIONS) {
        GameState.effects.explode(x + RandomUtil.nextFloat(10), y + RandomUtil.nextFloat(10));
        dieExplodeWait = 0;
      }
      
      if (!dead && deadWait > TIME_TO_WATCH_DEATH) {
        dead = true;
        gameMode.endGame();
      }
      return;
    }
    
    super.update(time);

    emitter.x = x;
    emitter.y = y;
    emitter.emitAngle = angle + 180;
    emitter.update(time);
    emitter.setEmitRate((long)((velocity.x * velocity.x + velocity.y * velocity.y) * 60 / 20000));

    boundsComponent.update(time);
    
  }
  
  public void draw(Canvas canvas) {
    
    if (dying || dead) {
      paint.setColor(Color.RED);
      paint.setStyle(Style.FILL);
    } else {
      super.draw(canvas);
    }
    
    canvas.save();

    canvas.translate(x, y);
    canvas.rotate(getAngleOffset() + angle);
    canvas.scale(scale, scale);
    
    canvas.drawPath(commonPolygon.path, paint);
    canvas.restore();
  }
  
  public void fire() {
    gun.fire();
  }
  
  public void fire(float angle) {
   
    gun.setAimAngle(angle);
    //((DirectionalGunControl) gun.control).angleOffset
    
    gun.fire();
  }
  
  @Override
  protected float getAngleOffset() {
    return ANGLE_OFFSET;
  }

  public void collide(PhysicalObject object, Vector2d avoidVector) {
    if (object instanceof Bullet) {
      health -= 5;
      if (health < 0) {
        health = 0;
        GameState.effects.hit(x, y, avoidVector);
        dying = true;
      }
    } else {
      super.collide(object, avoidVector);
    }
  }
}
