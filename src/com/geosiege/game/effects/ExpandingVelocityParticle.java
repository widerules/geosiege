package com.geosiege.game.effects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.zeddic.game.common.particle.Particle;
import com.zeddic.game.common.particle.ParticleData;

public class ExpandingVelocityParticle extends Particle {

  
  ////SETUP OBJECT SHAPE AND PAINT
  private static final Paint PAINT;

  static {
    PAINT = new Paint();
    PAINT.setColor(Color.YELLOW);
    //PAINT.setARGB(255, 90, 255, 0);
    PAINT.setStyle(Paint.Style.STROKE);
    PAINT.setStrokeWidth(3.5f);
    //GREEN_PAINT.setStrokeWidth(3);
    // paint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.OUTER));
    // blurFilter = new BlurMaskFilter(18, BlurMaskFilter.Blur.NORMAL);
  }
  
  private float startX = 0;
  private float startY = 0;
  
  /**
   * Creates a new particle at the origin with default values.
   */
  public ExpandingVelocityParticle() {
    this(0, 0);
  }
  
  /**
   * Creates a new particle at the given position with default values.
   */
  public ExpandingVelocityParticle(float x, float y) {
    super(x, y);

    paint = PAINT;
    /*paint = new Paint();
    paint.setColor(Color.YELLOW);
    paint.setStrokeWidth(5); */
  }
  
  @Override
  public void onEmit(ParticleData data) {
    startX = x;
    startY = y;
  }
  
  public void draw(Canvas canvas) {
    paint.setAlpha((int) alpha);
 
    /*canvas.save();

    canvas.translate(x, y);
    
    //canvas.drawPath(MONEY_SHAPE.path, PAINT);

    canvas.restore(); */
    
    canvas.drawLine(startX, startY, x, y, paint);
    
    // Draw the particle as a line from its current position to where it
    // used to be. The length of the line is based on its current velocity.
    //c.drawLine(x, y, x + -scaledVelocity.x, y + -scaledVelocity.y, paint);
    //c.drawCircle(x, y, scale, paint);
  }
  
}
