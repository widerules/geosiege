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

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.animation.Transitions;
import com.geosiege.common.util.Countdown;
import com.geosiege.common.util.Vector2d;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Bullet;


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
    
    // Start a spawning effect.
    GameState.effects.implode(x, y, spawnTime);
  }
  
  public void update(long time) {
    
    if (isSpawning()) {
      // Countdown to see if we are done.
      spawnCountdown.update(time);
      
      // If the spawn is complete, allow updates, otherwise quit.
      if (spawnCountdown.done) {
        spawnCountdown.reset();
      } else {
        return;
      }
    }
  
    super.update(time);
  }
  
  public void draw(Canvas canvas) {
    super.draw(canvas);
    
    if (isSpawning()) {
      paint.setAlpha((int) (255 * 
          Transitions.getProgress(Transitions.EXPONENTIAL, spawnCountdown.getProgress())));
    } else {
      paint.setAlpha(255);
    }
  }
  
  @Override
  public void collide(PhysicalObject object, Vector2d avoidVector) {
    super.collide(object, avoidVector);
    
    if (object instanceof Bullet) {
      die();
    }
  }
  
  public boolean isSpawning() {
    return spawnCountdown.counting;
  }
  
  public boolean isDead() {
    return !active;
  }
  
  public void die() {
    if (isDead())
      return;
    
    GameState.effects.explode(x, y);
    GameState.effects.explodeWithGravity(x, y, GameState.playerShip);
    GameState.stats.recordKill();
    
    kill();
  }
}
