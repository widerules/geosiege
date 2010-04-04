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
import com.geosiege.common.util.ObjectPool;
import com.geosiege.common.util.RandomUtil;
import com.geosiege.common.util.ObjectPool.ObjectBuilder;
import com.geosiege.game.ships.EnemyShip;

public class EnemySpawner extends GameObject {

  int maxShips;
  long cooldown;
  Class<? extends EnemyShip> shipClass;
  long lastSpawnTime;
  
  float x;
  float y;
  float xJitter;
  float yJitter;
  
  ObjectPool<EnemyShip> pool;
  
  public EnemySpawner(Class<? extends EnemyShip> shipClass, int maxShips, int cooldown) {
    this.shipClass = shipClass;
    this.maxShips = maxShips;
    this.cooldown = cooldown;
    lastSpawnTime = 0;
   
  }
  
  public void init() {
    createPool();
  }
  
  public void setSpawn(float x, float y, float xJitter, float yJitter) {
    this.x = x;
    this.y = y;
    this.xJitter = xJitter;
    this.yJitter = yJitter;
  }
  
  private void createPool() {
    
    pool = new ObjectPool<EnemyShip>(EnemyShip.class, maxShips, new ObjectBuilder<EnemyShip>() {
      @Override
      public EnemyShip get(int count) {
        
        try {
          EnemyShip enemy = (EnemyShip) shipClass.newInstance();
          enemy.x = 500;
          enemy.y = 500;
          enemy.active = false;
          return enemy;
          
        } catch (Exception e) {
          return null;
        }
      }
    });
  }
 
  private boolean canSpawn() {
    long now = System.currentTimeMillis();
    return  now - lastSpawnTime > cooldown;
  }
  
  private void recordSpawn() {
    lastSpawnTime = System.currentTimeMillis();
  }
  
  
  private void spawn() {

    EnemyShip ship = pool.take();
    if (ship == null)
      return;
    
    ship.reset();
    ship.x = x + RandomUtil.nextFloat(-xJitter, xJitter);
    ship.y = y + RandomUtil.nextFloat(-yJitter, yJitter);
    
    recordSpawn();
  }
  
  public void draw(Canvas c) {
    
    EnemyShip ship;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      ship = pool.items[i];
      if (ship.active) {
        ship.draw(c);
      }
    }
  }
  
  public void update(long time) {
    
    super.update(time);
    
    if (canSpawn())
      spawn();
        
    EnemyShip ship;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      ship = pool.items[i];
      
      if (ship.active) {
        
        if (ship.killed) {
          ship.active = false;
        } else {
          ship.update(time);
        }
        
        if (!ship.active) {
          pool.restore(ship);
        }
      }
    }
  }
}
