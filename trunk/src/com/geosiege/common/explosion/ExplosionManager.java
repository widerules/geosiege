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

package com.geosiege.common.explosion;

import android.graphics.Canvas;

import com.geosiege.common.GameObject;
import com.geosiege.common.util.ObjectPool;
import com.geosiege.common.util.Vector2d;
import com.geosiege.common.util.ObjectPool.ObjectBuilder;

public class ExplosionManager extends GameObject {

  ObjectPool<Explosion> explodePool;
  ObjectPool<HitExplosion> hitPool;
  
  public static ExplosionManager singleton;
  
  static { 
    singleton = new ExplosionManager();
  }
  
  private ExplosionManager() {
    createPool();
  }
  
  private void createPool() {
    explodePool = new ObjectPool<Explosion>(Explosion.class, 50, new ObjectBuilder<Explosion>() {
      @Override
      public Explosion get(int count) {
        Explosion explosion = new Explosion(0, 0);
        explosion.active = false;
        return explosion;
      }
    });
    
    hitPool = new ObjectPool<HitExplosion>(HitExplosion.class, 50, new ObjectBuilder<HitExplosion>() {
      @Override
      public HitExplosion get(int count) {
        HitExplosion explosion = new HitExplosion(0, 0);
        explosion.active = false;
        return explosion;
      }
    });
  }
  
  public Explosion explode(float x, float y) {
    Explosion explosion = explodePool.take();
    if (explosion == null)
      return null;
    
    explosion.x = x;
    explosion.y = y;
    explosion.ignite();
    return explosion;
  }
  
  public HitExplosion hit(float x, float y, Vector2d direction) { 
    HitExplosion explosion = hitPool.take();
    if (explosion == null)
      return null;
    
    float angle = (float) Math.toDegrees(Math.atan( direction.y / direction.x));
    if (direction.x < 0)
      angle += 180;
    explosion.emitter.emitAngle = angle;
    explosion.x = x;
    explosion.y = y;
    explosion.ignite();
    return explosion;
  }
  
  public void draw(Canvas canvas) {
    Explosion explosion;
    for ( int i = 0 ; i < explodePool.items.length ; i++) {
      explosion = explodePool.items[i];
      if (explosion.active)
        explosion.draw(canvas);
    }
    
    for ( int i = 0 ; i < hitPool.items.length ; i++) {
      explosion = hitPool.items[i];
      if (explosion.active)
        explosion.draw(canvas);
    }
  }
  
  public void update(long time) {
    Explosion explosion;
    for ( int i = 0 ; i < explodePool.items.length ; i++) {
      explosion = explodePool.items[i];
      if (explosion.active) {
        explosion.update(time);
        if (!explosion.active) {
          explodePool.restore(explosion);
        }
      }
    }
    
    HitExplosion hit; 
    for ( int i = 0 ; i < hitPool.items.length ; i++) {
      hit = hitPool.items[i];
      if (hit.active) {
        hit.update(time);
        if (!hit.active) {
          hitPool.restore(hit);
        }
      }
    }
  }
  
  public static ExplosionManager get() {
    return singleton;
  }
}
