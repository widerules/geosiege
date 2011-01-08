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
import com.geosiege.game.components.ParticleTail;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Bullet;
import com.geosiege.game.guns.Gun;
import com.geosiege.game.storage.GameStorage;
import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.collision.CollisionComponent;
import com.zeddic.game.common.collision.CollisionManager;
import com.zeddic.game.common.util.Bounds;
import com.zeddic.game.common.util.Circle;
import com.zeddic.game.common.util.ComponentManager;
import com.zeddic.game.common.util.Countdown;
import com.zeddic.game.common.util.Polygon;
import com.zeddic.game.common.util.Vector2d;
import com.zeddic.game.common.util.Polygon.PolygonBuilder;

public class PlayerShip extends Ship {
  
  private static final Paint PAINT;
  public static final Polygon SHAPE;
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

  public float maxHealth = 200;
  public float health = 200;
  private Countdown soundCountdown = new Countdown(400);
  private Gun gun;
  private ComponentManager components;
  private float speed;

  public PlayerShip() {
    this(0, 0);
  }
  
  public PlayerShip(float x, float y) {
    super(x, y);

    paint = new Paint(PAINT);
    setBounds(new Bounds(new Circle(8)));
    
    speed = GameStorage.upgrades.getSpeed();
    
    gun = GameStorage.upgrades.getGun();
    gun.setOwner(this);
    
    components = new ComponentManager(this);
    components.add(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    components.add(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE));
    components.add(gun);
    
    if (GameStorage.upgrades.isTailEnabled()) {
      components.add(new ParticleTail(this, 11));
    }
  }
  
  public void reset() {
    enable();
    gun.reset();
    
    health = maxHealth;
    
    paint.setColor(Color.BLUE);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(3);
    
    soundCountdown.restart();
  }
  
  public void spawn(float x, float y) {
    reset();
    this.x = x;
    this.y = y;
  }
  
  /*public void centerOnMap() {
    this.x = GameState.level.map.left + GameState.level.map.width / 2;
    this.y = GameState.level.map.top + GameState.level.map.height / 2;
  }*/
  
  public void updateSpeed(float scaleX, float scaleY) {
    this.setVelocity(scaleX * speed, scaleY * speed);
  }
  
  @Override
  public void update(long time) {
    
    if (isDead())
      return;
    
    super.update(time);
    
    soundCountdown.update(time);
    
    components.update(time);
  }
  
  void playSound() {
    //if (!soundCountdown.isDone())
    //  return;
    
    //GameResources.play(GameResources.SOUND_GUN_4);
    
    //soundCountdown.restart();
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
  
  public void fire() {
    gun.fire();
  }
  
  public void fire(float angle) {
    
    gun.setAimAngle(angle);    
    if(gun.fire()) {
      playSound();
    }
  }
  
  public boolean isDead() {
    return !active;
  }
  
  @Override
  protected float getAngleOffset() {
    return ANGLE_OFFSET;
  }
  
  @Override
  public void damage(float damage) {
    if (isDead())
      return;
    health -= damage;
    health = Math.max(0, health);
    if (health == 0) {
      die();
    }
  }
  
  public float getPercentHealth() {
    return health / maxHealth;
  }
  
  public void die() {
    if (isDead()) {
      return;
    }
    kill();
    GameState.geoEffects.shockwave(x, y);
    GameState.level.delayGame(5000);
    GameState.stockpiles.killAllEnemies();    
  }

  public void collide(PhysicalObject object, Vector2d avoidVector) {
    if (object instanceof Bullet) {
      damage(5);
    } else if (object instanceof EnemyShip) {
      damage(5);
      ((EnemyShip) object).damage(5);
    } else {
      super.collide(object, avoidVector);
    }
  }
}
