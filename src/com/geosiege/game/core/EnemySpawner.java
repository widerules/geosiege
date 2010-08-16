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

package com.geosiege.game.core;

import android.graphics.Canvas;

import com.geosiege.common.GameObject;
import com.geosiege.common.animation.Transition;
import com.geosiege.common.animation.Transitions;
import com.geosiege.common.util.ObjectPoolManager;
import com.geosiege.common.util.RandomUtil;
import com.geosiege.game.ships.EnemyShip;

/**
 * TODO: DELETE THIS CLASS.
 */
public class EnemySpawner<T extends EnemyShip> extends GameObject {

  int maxShips;
  //long startCooldown;
  //long endCooldown;
  //long rampupTime;
  //long startTime;
  //long cooldown;
  Class<T> shipClass;
  long lastSpawnTime;
  
  float x;
  float y;
  float xJitter;
  float yJitter;
  
  Transition cooldownTransition;
  
  ObjectPoolManager<T> ships;
  
  public EnemySpawner(Class<T> shipClass, int maxShips, int startCooldown, int endCooldown, long rampupTime) {
    this.shipClass = shipClass;
    this.maxShips = maxShips;
    //this.startCooldown = startCooldown;
    //this.endCooldown = endCooldown;
    //this.rampupTime = rampupTime;
    //this.startTime = System.currentTimeMillis();
    
    cooldownTransition = new Transition(startCooldown, endCooldown, rampupTime, Transitions.LINEAR);
    
    this.lastSpawnTime = 0;
  }
  
  public void init() {
    ships = new ObjectPoolManager<T>(shipClass, maxShips);
  }
  
  public void setSpawn(float x, float y, float xJitter, float yJitter) {
    this.x = x;
    this.y = y;
    this.xJitter = xJitter;
    this.yJitter = yJitter;
  }
  
  private boolean canSpawn(long time) {
    
    /*double progress = Math.max(
        1, (double) (System.currentTimeMillis() - startTime) / (double) rampupTime);
    progress = Transitions.getProgress(Transitions.EXPONENTIAL, progress);
    cooldown = (long) (startCooldown + (endCooldown - startCooldown) * progress); */
    long cooldown = (long) cooldownTransition.update(time);
    
    long now = System.currentTimeMillis();
    return  now - lastSpawnTime > cooldown;
  }
  
  private void recordLastSpawnTime() {
    lastSpawnTime = System.currentTimeMillis();
  }
  
  
  private void spawn() {

    EnemyShip ship = ships.take();
    if (ship == null)
      return;

    float spawnX = x + RandomUtil.nextFloat(-xJitter, xJitter);
    float spawnY = y + RandomUtil.nextFloat(-yJitter, yJitter);
    ship.spawn(spawnX, spawnY, 2000);

    recordLastSpawnTime();
  }
  
  public void draw(Canvas c) {
    ships.draw(c);
  }
  
  public void update(long time) {
    
    super.update(time);
    
    if (canSpawn(time))
      spawn();
        
    ships.update(time);
  }
}
