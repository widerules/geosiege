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
import com.geosiege.common.particle.MoneyParticle;
import com.geosiege.common.particle.ParticleEmitter;
import com.geosiege.common.particle.ParticleEmitter.ParticleEmitterBuilder;
import com.geosiege.common.util.Bounds;
import com.geosiege.common.util.Circle;
import com.geosiege.common.util.ComponentManager;
import com.geosiege.common.util.Countdown;
import com.geosiege.common.util.Polygon;
import com.geosiege.common.util.Vector2d;
import com.geosiege.common.util.Polygon.PolygonBuilder;
import com.geosiege.game.components.MapBoundsComponent;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Bullet;
import com.geosiege.game.guns.Gun;

public class PlayerShip extends Ship {
  
  private static final long TIME_TO_WATCH_DEATH = 0;
  private static final Paint PAINT;
  private static final Polygon SHAPE;
  private static float ANGLE_OFFSET = -90;
  static {
    PAINT = new Paint();
    PAINT.setColor(Color.BLUE);
    PAINT.setStyle(Paint.Style.STROKE);
    PAINT.setStrokeWidth(3);

    SHAPE = new PolygonBuilder()
        .add(0, 17)
        .add(12, -8)
        .add(0, -16)
        .add(-12, -8)
        .build();
  }
  
  private ParticleEmitter emitter;
  private Gun gun;
  private ComponentManager components;
  private boolean dying;
  private boolean dead;
  private Countdown dyingCountdown;
  
  public PlayerShip() {
    this(0, 0);
  }
  
  public PlayerShip(float x, float y) {
    super(x, y);
    
    dyingCountdown = new Countdown(TIME_TO_WATCH_DEATH);
    dying = false;
    dead = false;
    health = GameState.player.maxHealth;
    paint = new Paint(PAINT);
    setBounds(new Bounds(new Circle(8)));

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
    
    components = new ComponentManager(this);
    components.add(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    components.add(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE));
    components.add(gun);
  }
  
  public void reset() {
    dyingCountdown.reset();
    dying = false;
    dead = false;
    health = GameState.player.maxHealth;
    gun.reset();
    
    paint.setColor(Color.BLUE);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(3);
  }
  
  public void setAngle(float angle) {
    if (!dead) {
      super.setAngle(angle);
    }
  }
  
  public void centerOnMap() {
    this.x = GameState.level.map.left + GameState.level.map.width / 2;
    this.y = GameState.level.map.top + GameState.level.map.height / 2;
  }
  
  @Override
  public void update(long time) {
    
    if (dead)
      return;
    
    if (dying) {
      dyingCountdown.update(time);
      if (dyingCountdown.isDone()) {
        dead = true;
      }
      return;
    }
    
    super.update(time);

    emitter.x = x;
    emitter.y = y;
    emitter.emitAngle = angle + 180;
    
    components.update(time);
  }
  
  @Override
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
    
    canvas.drawPath(SHAPE.path, paint);
    canvas.restore();
    
    components.draw(canvas);
  }
  
  public void fire() {
    gun.fire();
  }
  
  public void fire(float angle) {
    gun.setAimAngle(angle);    
    gun.fire();
  }
  
  public boolean isDead() {
    return dead;
  }
  
  public boolean isDying() {
    return dying;
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
        dyingCountdown.start();
        dying = true;
      }
    } else if (object instanceof MoneyParticle) {
      GameState.player.addExp(5);
    } else {
      super.collide(object, avoidVector);
    }
  }
}
