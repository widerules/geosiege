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
import com.zeddic.game.common.particle.BasicParticleOptions;
import com.zeddic.game.common.particle.ParticleEmitter.ParticleEmitterBuilder;

public class TeleportExplosion extends PhysicalObject {

  public static final int NUMBER_OF_SHARDS = 8;
  
  ParticleEmitter emitter;
  
  public TeleportExplosion() {
    this(0, 0);
  }
  
  public TeleportExplosion(float x, float y) {
    super(x, y);
    createEmitter();
  }
  
  protected void createEmitter() {
    emitter = new ParticleEmitterBuilder()
        .at(x, y)
        .withEmitMode(ParticleEmitter.MODE_OMNI)
        .withEmitSpeedJitter(2)
        .withEmitLife(5000)
        .withParticleSpeed(20)
        .withParticleLife(5000)
        .withMaxParticles(NUMBER_OF_SHARDS)
        .withEmitRate(800)
        .withParticleAcceleration(-3)
        .withEmitCycle(false)
        .withParticleClass(ShockwaveParticle.class)
        .withDataForParticles(new BasicParticleOptions(BasicParticleOptions.createPaint(255, 204, 0), 5))
        .withGravityWell(null, 40)
        .withGravityWellCollide(true)
        .withGravityWellDispawnDistance(25)
        .withGravityWellMaxDistance(0)
        .withMaxParticleSpeed(70)
        .build();
  }
  
  public void ignite(PhysicalObject target) {
    enable();
    emitter.pGravityWell = target;
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
