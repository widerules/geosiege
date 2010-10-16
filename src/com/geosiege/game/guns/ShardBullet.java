package com.geosiege.game.guns;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.zeddic.game.common.util.Bounds;
import com.zeddic.game.common.util.Circle;
import com.zeddic.game.common.util.Polygon;
import com.zeddic.game.common.util.Polygon.PolygonBuilder;

public class ShardBullet extends Bullet {

  private static final Paint PAINT;
  private static float ANGLE_OFFSET = 90;
  private static Polygon SHAPE;
  static {
    PAINT = new Paint();
    PAINT.setARGB(255, 255, 255, 0);
    PAINT.setStyle(Paint.Style.FILL);
    PAINT.setStrokeWidth(2);
    
    SHAPE = new PolygonBuilder()
        .add(-4, 0)
        .add(-2, -6)
        .add(2, -6)
        .add(4, 0)
        .build();
  }
  
  public ShardBullet() {
    this(0, 0);
  }
  
  public ShardBullet(float x, float y) {
    super(x, y);
    
    bounds = new Bounds(new Circle(8));
  }
  
  @Override
  public void draw(Canvas canvas) {
    canvas.save();

    canvas.translate(x, y);
    canvas.rotate(getAngleOffset() + angle);
    canvas.drawPath(SHAPE.path, PAINT);
    canvas.restore();
  }
  
  @Override
  protected float getAngleOffset() {
    return ANGLE_OFFSET;
  }
}
