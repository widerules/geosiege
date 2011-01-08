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

import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Bullet;
import com.geosiege.game.storage.GameStorage;
import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.transistions.Transitions;
import com.zeddic.game.common.util.Countdown;
import com.zeddic.game.common.util.Vector2d;


public class EnemyShip extends Ship {
  
  Countdown spawnCountdown = new Countdown(0);
  public int exp = 1;

  public EnemyShip(float x, float y) {
    super(x, y);
  }
  
  public void reset() {
    enable();
  }
  
  public void spawn(float x, float y, int spawnTime) {
    reset();
    this.x = x;
    this.y = y;

    spawnCountdown.reset(spawnTime);
    spawnCountdown.start();
  }
  
  public void update(long time) {
    
    if (isSpawning()) {
      // Countdown to see if we are done.
      spawnCountdown.update(time);
      
      // If the spawn is complete, allow updates, otherwise quit.
      if (spawnCountdown.done) {
        spawnCountdown.reset();
        scale = 1;
      } else {
        return;
      }
    }
  
    super.update(time);
  }
  
  public void draw(Canvas canvas) {
    super.draw(canvas);
    
    if (isSpawning()) {
      double progress = Transitions.getProgress(Transitions.EXPONENTIAL, spawnCountdown.getProgress());
      paint.setAlpha((int) Math.max(255, 255 * progress));
      scale = (float) progress; 
    } else {
      paint.setAlpha(255);
    }
  }
  
  @Override
  public void collide(PhysicalObject object, Vector2d avoidVector) {
    super.collide(object, avoidVector);
    
    if (object instanceof Bullet) {
      damage(20);
    }
  }
  
  public boolean isSpawning() {
    return spawnCountdown.counting;
  }
  
  public boolean isDead() {
    return !active;
  }
  
  @Override
  public void damage(float damage) {
    health -= damage;
    if (health <= 0) {
      health = 0;
      die();
    }
  }
  
  public void die() {
    if (isDead())
      return;
    
    GameState.geoEffects.shockwave(x, y);
    GameState.player.recordKill(getClass());
    GameStorage.stats.recordKill();
    
    kill();
  }
}
