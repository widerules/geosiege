package com.geosiege.game.components;

import android.graphics.Canvas;

import com.geosiege.game.effects.ShockwaveParticle;
import com.zeddic.game.common.Component;
import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.particle.BasicParticleOptions;
import com.zeddic.game.common.particle.ParticleEmitter;
import com.zeddic.game.common.particle.ParticleEmitter.ParticleEmitterBuilder;
import com.zeddic.game.common.transistions.Range;
import com.zeddic.game.common.transistions.RangeConverter;

/**
 * A component that creates a tail behind the parent object composed of
 * scattering particles. 
 */
public class ParticleTail extends Component {

  private static final Range SPEED_INPUT_RANGE = new Range(0, 60 * 60);
  private static final Range TAIL_EMIT_ANGLE_RANGE = new Range(12, 5);
  private static final Range TAIL_EMIT_RATE_RANGE = new Range(2, 50);
  private static final Range TAIL_EMIT_SPEED_RANGE = new Range(10, 25);
  private static final RangeConverter EMIT_ANGLE = new RangeConverter(SPEED_INPUT_RANGE, TAIL_EMIT_ANGLE_RANGE);
  private static final RangeConverter EMIT_RATE = new RangeConverter(SPEED_INPUT_RANGE, TAIL_EMIT_RATE_RANGE);
  private static final RangeConverter EMIT_SPEED = new RangeConverter(SPEED_INPUT_RANGE, TAIL_EMIT_SPEED_RANGE);
  
  private ParticleEmitter emitter;
  private PhysicalObject parent;
  private float offset;
  
  public ParticleTail(
      PhysicalObject parent,
      float offset) {
    
    this.parent = parent;
    this.offset = offset;
    
    emitter = new ParticleEmitterBuilder()
        .at(0, 0)
        .withEmitMode(ParticleEmitter.MODE_DIRECTIONAL)
        .withEmitAngleJitter(10)
        .withEmitSpeedJitter(5)
        .withParticleSpeed(25)
        .withParticleAlphaRate(-5)
        .withParticleLife(500)
        .withMaxParticles(200)
        .withEmitRate(60)
        .withParticleClass(ShockwaveParticle.class)
        .withDataForParticles(new BasicParticleOptions(BasicParticleOptions.createPaint(255, 196, 0)))
        .build();  
  }
  
  @Override
  public void reset() {
    emitter.reset();
  }
  
  @Override
  public void update(long time) {
    emitter.x = parent.x - offset * (float) Math.cos(Math.toRadians(parent.angle));
    emitter.y = parent.y - offset * (float) Math.sin(Math.toRadians(parent.angle));
    
    float speed = 
        parent.velocity.x * parent.velocity.x +
        parent.velocity.y * parent.velocity.y;
    
    emitter.emitAngle = parent.angle + 180;
    emitter.emitAngleJitter = EMIT_ANGLE.convert(speed);
    emitter.setEmitRate((long) EMIT_RATE.convert(speed));
    emitter.pSpeed = EMIT_SPEED.convert(speed);
    emitter.update(time);
  }
  
  @Override
  public void draw(Canvas c) {
    emitter.draw(c);
  }
}
