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

package com.geosiege.game.guns;

import android.graphics.Canvas;

import com.geosiege.common.util.ObjectPool;
import com.geosiege.common.util.ObjectPool.ObjectBuilder;
import com.geosiege.game.ships.EnemyShip;

public class BulletGun extends Gun {
  
  ObjectPool<Bullet> pool;
  
  public void init() {
    createPool();
  }

  private void createPool() {
    pool = new ObjectPool<Bullet>(Bullet.class, maxBullets, new ObjectBuilder<Bullet>() {
      @Override
      public Bullet get(int count) {
        Bullet bullet = new Bullet(0, 0);
        bullet.active = false;
        bullet.firedByEnemy = (owner instanceof EnemyShip);
        
        return bullet;
      }
    });
  }
  
  public void fire() {
    if (!canFire() || !shouldFire())
      return;
    
    aimGun();
    
    Bullet bullet = pool.take();
    if (bullet == null) {
      return;
    }
    bullet.x = owner.x + xOffset;
    bullet.y = owner.y + yOffset;
    bullet.setAngle(aimAngle);
    bullet.setVelocityBySpeed(aimAngle, bulletSpeed);
    bullet.offset(fireOffset);
    bullet.active = true;
    bullet.life = 0;
    
    recordFire();
  }
  
  public void draw(Canvas canvas) {
    Bullet bullet;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      bullet = pool.items[i];
      if (bullet.active)
        bullet.draw(canvas);
    }
  }

  public void update(long time) {
    
    super.update(time);
    
    Bullet bullet;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      bullet = pool.items[i];
      if (bullet.active) {
        bullet.update(time);
        
        // If the bullet died during its update, restore it to the pool.
        if (!bullet.active) {
          pool.restore(bullet);
        }
      }
    }
  }
  
  public void reset() {
    Bullet bullet;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      bullet = pool.items[i];
      if (bullet.active) {
        bullet.active = false;
        pool.restore(bullet);
      }
    }
  }
}
