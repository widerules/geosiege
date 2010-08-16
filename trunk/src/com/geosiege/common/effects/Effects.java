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

package com.geosiege.common.effects;

import android.graphics.Canvas;

import com.geosiege.common.GameObject;
import com.geosiege.common.PhysicalObject;
import com.geosiege.common.util.ObjectPoolManager;
import com.geosiege.common.util.Vector2d;

public class Effects extends GameObject {
  
  ObjectPoolManager<Explosion> regularExplosions;
  ObjectPoolManager<HitExplosion> hitExplosions;
  ObjectPoolManager<GravityExplosion> gravityExplosions;
  ObjectPoolManager<Implosion> spawnImplosions;
  
  public static Effects singleton;
  
  static { 
    singleton = new Effects();
  }
  
  private Effects() {
    createPools();
  }
  
  private void createPools() {
    regularExplosions = new ObjectPoolManager<Explosion>(Explosion.class);
    hitExplosions = new ObjectPoolManager<HitExplosion>(HitExplosion.class);
    gravityExplosions = new ObjectPoolManager<GravityExplosion>(GravityExplosion.class);
    spawnImplosions = new ObjectPoolManager<Implosion>(Implosion.class);
  }
  
  public Explosion explode(float x, float y) {
    Explosion explosion = regularExplosions.take();
    if (explosion == null)
      return null;
    
    explosion.x = x;
    explosion.y = y;
    explosion.ignite();
    return explosion;
  }
  
  public HitExplosion hit(float x, float y, Vector2d direction) { 
    HitExplosion explosion = hitExplosions.take();
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
  
  public GravityExplosion explodeWithGravity(
      float x, float y, PhysicalObject dest) {
    
    GravityExplosion explosion = gravityExplosions.take();
    if (explosion == null)
      return null;
    
    explosion.x = x;
    explosion.y = y;
    explosion.ignite(dest);
    
    return explosion;
  }
  
  public Implosion implode(float x, float y, long time) {
    
    Implosion implosion = spawnImplosions.take();
    if (implosion == null)
      return null;
    
    implosion.x = x;
    implosion.y = y;
    implosion.emitter.emitLife = time;
    implosion.ignite();
    
    return implosion;
  }
  
  public void reset() {
    regularExplosions.reclaimPool();
    hitExplosions.reclaimPool();
    gravityExplosions.reclaimPool();
    spawnImplosions.reclaimPool();
  }
  
  public void draw(Canvas canvas) {
    regularExplosions.draw(canvas);
    hitExplosions.draw(canvas);
    gravityExplosions.draw(canvas);
    spawnImplosions.draw(canvas);
  }
  
  public void update(long time) {
    regularExplosions.update(time);
    hitExplosions.update(time);
    gravityExplosions.update(time);
    spawnImplosions.update(time);
  }
  
  public static Effects get() {
    return singleton;
  }
}
