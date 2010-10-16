package com.geosiege.game.effects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.geosiege.game.components.MapBoundsComponent;
import com.zeddic.game.common.particle.BasicParticleOptions;
import com.zeddic.game.common.particle.Particle;
import com.zeddic.game.common.particle.ParticleData;
import com.zeddic.game.common.transistions.Range;
import com.zeddic.game.common.transistions.RangeConverter;
import com.zeddic.game.common.util.ComponentManager;

public class ShockwaveParticle extends Particle {

  private static final Paint DEFAULT_PAINT; 
  static {
    DEFAULT_PAINT = new Paint();
    DEFAULT_PAINT.setStyle(Paint.Style.STROKE);
    DEFAULT_PAINT.setStrokeWidth(2);
    DEFAULT_PAINT.setColor(Color.WHITE);
  }
  
  private static final Range SPEED_INPUT_RANGE = new Range(15 * 15, 25 * 25);
  private static final Range DRAW_LENGTH = new Range(5, 10);
  private static final RangeConverter DRAW_LENGTH_CALCULATOR = new RangeConverter(SPEED_INPUT_RANGE, DRAW_LENGTH);
  
  private ComponentManager components;
  private BasicParticleOptions options;
  
  /**
   * Creates a new particle at the origin with default values.
   */
  public ShockwaveParticle() {
    this(0, 0);
  }
  
  /**
   * Creates a new particle at the given position with default values.
   */
  public ShockwaveParticle(float x, float y) {
    super(x, y);

    components = new ComponentManager(this);
    components.add(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_BOUNCE));
    paint = DEFAULT_PAINT;
    /*paint = new Paint();
    paint.setColor(Color.YELLOW);
    paint.setStrokeWidth(5); */
  }
  
  @Override
  public void onEmit(ParticleData data) {
    this.options = (BasicParticleOptions) data;
    this.paint = options.color;
  }
  
  public void draw(Canvas canvas) {
    paint.setAlpha((int) alpha);
    paint.setStrokeWidth(options.particleWidth);
    
    float length = DRAW_LENGTH_CALCULATOR.convert(
        scaledVelocity.x * scaledVelocity.x + scaledVelocity.y * scaledVelocity.y);

    canvas.drawLine(x, y, x + scaledVelocity.x * length, y + scaledVelocity.y * length, paint);
  }
  
  @Override
  public void update(long time) {
    super.update(time);
    components.update(time);
  }
}
