package com.geosiege.common.effects;

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.particle.MoneyParticle;
import com.geosiege.common.particle.ParticleEmitter;
import com.geosiege.common.particle.ParticleEmitter.ParticleEmitterBuilder;

public class GravityExplosion extends Explosion {

  public GravityExplosion() {
    super(0, 0);
  }
  
  public GravityExplosion(float x, float y) {
    super(x, y);
  }

  protected void createEmitter() {
    emitter = new ParticleEmitterBuilder()
        .at(x, y)
        .withEmitMode(ParticleEmitter.MODE_OMNI)
        .withParticleSpeed(10)
        .withEmitSpeedJitter(8)
        .withEmitLife(20 * 1000)
        .withEmitCycle(false)
        .withParticleSpeed(6)
        .withMaxParticleSpeed(60)
        .withParticleAlpha(100)
        .withParticleAlphaRate(0)
        .withParticleLife(20 * 1000)
        .withMaxParticles(5)
        .withEmitRate(0)
        .withParticleClass(MoneyParticle.class)
        .withGravityWellMaxDistance(200)
        .withGravityWellDispawnDistance(25)
        .build();
    emitter.pGravityWellForce = 25;
  }
  
  public void ignite(PhysicalObject destination) {
    emitter.reset();
    emitter.life = 0;
    emitter.hasLife = true;
    active = true;
    
    emitter.pGravityWell = destination;
  }
  
  public void update(long time) {
    super.update(time);
    emitter.x = this.x;
    emitter.y = this.y;
    emitter.update(time);
    
    if (!emitter.hasLife) {
      active = false;
    }
  }
}