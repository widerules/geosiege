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
import com.geosiege.common.util.Vector2d;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Bullet;


public class EnemyShip extends Ship {
   
  //public boolean killed;
  
  protected static final long TIME_TO_SPAWN = 3000;
  
  public int exp = 1;
  public boolean spawning = false;
  public long spawnTime;

  public EnemyShip(float x, float y) {
    super(x, y);
  }
  
  public void reset() {
    enable();
    spawning = false;
  }
  
  public void spawn(float x, float y) {
    reset();
    this.x = x;
    this.y = y;
    spawning = true;
    spawnTime = System.currentTimeMillis();
    
    // Start a spawning effect.
    GameState.effects.implode(x, y, TIME_TO_SPAWN);
  }
  
  private void handleSpawning() {
    if (System.currentTimeMillis() > spawnTime + TIME_TO_SPAWN) {
      spawning = false;
    }
  }
  
  public void update(long time) {
    handleSpawning();
    if (spawning)
      return;
    
    super.update(time);
  }
  
  public void draw(Canvas canvas) {
    super.draw(canvas);
    
    if (spawning) {
      paint.setAlpha((int) (255 * Transitions.getProgress(
          Transitions.EXPONENTIAL,
          (double) (System.currentTimeMillis() - spawnTime) /
              (double) TIME_TO_SPAWN)));
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
  
  public boolean isDead() {
    return !active;
  }
  
  public void die() {
    if (isDead())
      return;
    
    GameState.effects.explode(x, y);
    GameState.effects.explodeWithGravity(x, y, GameState.playerShip);
    //GameState.player.addExp(exp);
    kill();
  }
}
