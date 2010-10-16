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

package com.geosiege.game.effects;

import android.graphics.Canvas;

import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.particle.ParticleEmitter;
import com.zeddic.game.common.particle.ParticleEmitter.ParticleEmitterBuilder;

public class PewExplosion extends PhysicalObject {

  ParticleEmitter emitter;
  
  public PewExplosion() {
    this(0, 0);
  }
  
  public PewExplosion(float x, float y) {
    super(x, y);
    createEmitter();
  }
  
  protected void createEmitter() {
    emitter = new ParticleEmitterBuilder()
        .at(x, y)
        .withEmitMode(ParticleEmitter.MODE_OMNI)
        .withEmitSpeedJitter(2)
        .withEmitLife(400)
        .withParticleSpeed(12)
        .withParticleAlphaRate(-6)
        .withParticleLife(400)
        .withMaxParticles(5)
        .withEmitRate(1000)
        .withEmitCycle(false)
        .withParticleClass(ExpandingVelocityParticle.class)
        .build();
  }
  
  public void ignite() {
    enable();
    emitter.reset();
  }
  
  public void draw(Canvas canvas) {
    emitter.draw(canvas);
  }
  
  public void update(long time) {
    super.update(time);
    emitter.x = this.x;
    emitter.y = this.y;
    emitter.update(time);
    if (!emitter.active)
      kill();
  }
}
