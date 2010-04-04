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

package com.geosiege.common.particle;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Path;
import android.util.Log;

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.util.ObjectPool;
import com.geosiege.common.util.Vector2d;
import com.geosiege.common.util.ObjectPool.ObjectBuilder;

public class ParticleEmitter extends PhysicalObject {

  public static int MODE_DIRECTIONAL = 0;
  public static int MODE_OMNI = 1;
  
  public int emitMode;
  public float emitAngle;
  public float emitAngleJitter;
  public float emitSpeedJitter;
  public long emitRate;
  public long emitLife;
  public long life;
  public float pAcceleration;
  public float pSpeed;
  public float pLife;
  public float pAlpha;
  public float pAlphaRate;
  public Path pPath;
  public Vector2d gravity;
  public boolean hasLife;
  
  
  private int maxParticles;
  private Class<? extends Particle> pClass;
  
  long fireCooldown;
  long lastFire;
  Random random;
  
  ObjectPool<Particle> pool;
  
  public ParticleEmitter(float x, float y) {
    super(x, y);
    random = new Random();
  }
  
  public void init() {
    
    active = true;
    hasLife = true;
    life = 0;
    setEmitRate(emitRate);
    
    lastFire = System.currentTimeMillis();
    
    pool = new ObjectPool<Particle>(Particle.class, maxParticles, new ObjectBuilder<Particle>() {
      @Override
      public Particle get(int count) {
        Particle particle = null;
        try {
          particle = pClass.newInstance();
          particle.active = false;
        } catch (IllegalAccessException e) {
          Log.e(this.getClass().toString(), "Error creating particle", e);
        } catch (InstantiationException e) {
          Log.e(this.getClass().toString(), "Error creating particle", e);
        }
        return particle;
      }
    });
  }
  
  public void setEmitRate(long rate) {
    this.emitRate = rate;
    fireCooldown = (emitRate == 0 ? Long.MAX_VALUE : 1000 / emitRate);
  }

  public void changeGravity(int x, int y, float force) {
    gravity.x = this.x - x;
    gravity.y = this.y - y;
    gravity.normalize();
    gravity.scale(force);
  }
  
  float tempEmitAngle;
  float tempEmitSpeed;
  public boolean emmit() {
    Particle particle = pool.take();
    if (particle == null)
      return false;
    particle.active = true;
    particle.x = x;
    particle.y = y;
    particle.life = 0;
    particle.maxLife = pLife;
    particle.acceleration = pAcceleration;
    particle.alpha = pAlpha;
    particle.alphaRate = pAlphaRate;
    
    if (emitMode == MODE_DIRECTIONAL) {
      tempEmitAngle = emitAngle + -emitAngleJitter + random.nextFloat() * emitAngleJitter * 2;
    } else {
      tempEmitAngle = random.nextFloat() * 360;
    }
    
    tempEmitSpeed = pSpeed + -emitSpeedJitter + random.nextFloat() * emitSpeedJitter * 2;
    particle.setVelocityBySpeed(tempEmitAngle, tempEmitSpeed);
    
    return true;
  }
  
  public void draw(Canvas canvas) {
    Particle particle;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      particle = pool.items[i];
      if (particle.active)
        particle.draw(canvas);
    }
  }
  
  long now;
  long passedTime; 
  long timesToFire;
  private void emmitNeededParticles(long time) {
    now = System.currentTimeMillis();
    passedTime = now - lastFire;
    if (passedTime > fireCooldown) {
      timesToFire = passedTime / fireCooldown;
      for (int i = 0 ; i < timesToFire ; i++) {
        if(!emmit())
          break;
      }
      lastFire = now - passedTime % fireCooldown;
    }
  }
  

  public void update(long time) {
    life += time;
    if (life <= emitLife || emitLife == 0) {
      emmitNeededParticles(time);
    } else {
      hasLife = false;
    }

    Particle particle;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      particle = pool.items[i];
      if (particle.active) {
        particle.update(time);
        
        // Clean up the particle once its life has expired.
        if (!particle.active || !hasLife) {
          pool.restore(particle);
        }
      }
    }
  }
  
  public static class ParticleEmitterBuilder {
    
    float x = 0;
    float y = 0;
    int emitMode = MODE_DIRECTIONAL;
    float emitAngle = 0;
    float emitAngleJitter = 0;
    float emitSpeedJitter = 0;
    long emitRate = 10;
    long emitLife = 0;
    float pAcceleration = 0;
    float pSpeed = 0;
    float pLife = 1000;
    float pAlpha = 255;
    float pAlphaRate = 0;
    Vector2d gravity = new Vector2d(0, 0);
    Class<? extends Particle> pClass = Particle.class;
    Path pPath = null;
    int maxParticles = 100;
    
    public ParticleEmitterBuilder at(float x, float y) {
      this.x = x;
      this.y = y;
      return this;
    }
    
    public ParticleEmitterBuilder withEmitMode(int emitMode) {
      this.emitMode = emitMode;
      return this;
    }
    
    public ParticleEmitterBuilder withEmitAngle(float emitAngle) {
      this.emitAngle = emitAngle;
      return this;
    }
    
    public ParticleEmitterBuilder withEmitRate(long emitRate) {
      this.emitRate = emitRate;
      return this;
    }
    
    public ParticleEmitterBuilder withEmitAngleJitter(float emitAngleJitter) {
      this.emitAngleJitter = emitAngleJitter;
      return this;
    }
    
    public ParticleEmitterBuilder withEmitSpeedJitter(float pEmitSpeedJitter) {
      this.emitSpeedJitter = pEmitSpeedJitter;
      return this;
    }
    
    public ParticleEmitterBuilder withEmitLife(long emitLife) {
      this.emitLife = emitLife;
      return this;
    }
    
    public ParticleEmitterBuilder withMaxParticles(int maxParticles) {
      this.maxParticles = maxParticles;
      return this;
    }
    
    public ParticleEmitterBuilder withParticleAcceleration(float pAcceleration) {
      this.pAcceleration = pAcceleration;
      return this;
    }
    
    public ParticleEmitterBuilder withParticleSpeed(float pSpeed) {
      this.pSpeed = pSpeed;
      return this;
    }
    
    public ParticleEmitterBuilder withParticleLife(float pLife) {
      this.pLife = pLife;
      return this;
    }
    
    public ParticleEmitterBuilder withParticleAlpha(float pAlpha) {
      this.pAlpha = pAlpha;
      return this;
    }
    
    public ParticleEmitterBuilder withParticleAlphaRate(float pAlphaRate) {
      this.pAlphaRate = pAlphaRate;
      return this;
    }
    
    public ParticleEmitterBuilder withParticlePath(Path pPath) {
      this.pPath = pPath;
      return this;
    }
    
    public ParticleEmitterBuilder withParticleClass(Class<? extends Particle> pClass) {
      this.pClass = pClass;
      return this;
    }
    
    public ParticleEmitterBuilder withGravity(Vector2d gravity) {
      this.gravity = gravity;
      return this;
    }
 
    public ParticleEmitter build() {
      ParticleEmitter emitter = new ParticleEmitter(x, y);
      emitter.emitMode = emitMode;
      emitter.emitAngle = emitAngle;
      emitter.emitAngleJitter = emitAngleJitter;
      emitter.emitSpeedJitter = emitSpeedJitter;
      emitter.emitRate = emitRate;
      emitter.emitLife = emitLife;
      emitter.pAcceleration = pAcceleration;
      emitter.pSpeed = pSpeed;
      emitter.pLife = pLife;
      emitter.pAlpha = pAlpha;
      emitter.pAlphaRate = pAlphaRate;
      emitter.pPath = pPath;
      emitter.pClass = pClass;
      emitter.gravity = gravity;
      emitter.maxParticles = maxParticles;
      
      emitter.init();
      
      
      return emitter;
    }
  }
}
