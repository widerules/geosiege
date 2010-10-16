package com.geosiege.game.ships;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.geosiege.game.components.MapBoundsComponent;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Bullet;
import com.geosiege.game.guns.Gun;
import com.geosiege.game.guns.control.DirectionalGunControl;
import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.collision.CollisionComponent;
import com.zeddic.game.common.collision.CollisionManager;
import com.zeddic.game.common.util.Bounds;
import com.zeddic.game.common.util.Circle;
import com.zeddic.game.common.util.ComponentManager;
import com.zeddic.game.common.util.Countdown;
import com.zeddic.game.common.util.Polygon;
import com.zeddic.game.common.util.Vector2d;
import com.zeddic.game.common.util.Polygon.PolygonBuilder;

public class Arrow extends EnemyShip {

  ////SETUP OBJECT SHAPE AND PAINT
  private static final Paint PAINT;
  private static final Polygon SHAPE;
  private static final float ANGLE_OFFSET = -90;
  static {
    PAINT = new Paint();
    PAINT.setColor(Color.argb(255, 255, 255, 255));
    PAINT.setStyle(Paint.Style.STROKE);
    PAINT.setStrokeWidth(2);
    
    SHAPE = new PolygonBuilder()
      .add(-8, -14)
      .add(0, 14)
      .add(8, -14)
      .build();
  }
  
  private static final int SPEED = 40;
  
  private ComponentManager components;
  private Countdown bounceCountdown = new Countdown(100);
  private Gun gun;
  
  public Arrow() {
    this(0, 0);
  }
  
  public Arrow(float x, float y) {
    super(x, y);
    
    this.exp = 20;
    this.paint = PAINT;
    this.bounds = new Bounds(new Circle(14));
    this.setVelocityBySpeed(90, SPEED);
    this.matchAngleWithVelocity();
    
    gun = Arsenal.getPeaShooter(this);
    gun.setGunControl(new DirectionalGunControl(this));
    gun.setAutoFire(true);
    gun.setFireCooldown(1000);
    
    components = new ComponentManager(this);
    components.add(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    components.add(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE));
    components.add(gun); 
    
    reset();
  }
  
  @Override
  public void spawn(float x, float y, int spawnTime) {
    super.spawn(x, y, spawnTime);
    setVelocityBySpeed(SPEED);
  }
  
  @Override
  public void reset() {
    super.reset(); 
    bounceCountdown.restart();
    gun.reset();
  }
  
  @Override
  public void update(long time) {
    super.update(time);
    if (isSpawning())
      return;
    
    components.update(time);
    bounceCountdown.update(time);
  }
  
  @Override
  protected float getAngleOffset() {
    return ANGLE_OFFSET;
  }
  
  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    
    canvas.save();
    canvas.translate(x, y);
    canvas.rotate(getAngleOffset() + angle);
    canvas.scale(scale, scale);
    canvas.drawPath(SHAPE.path, paint);
    canvas.restore();

    components.draw(canvas);
  }

  public void bounce(Vector2d avoidVector) {
    if (Math.abs(avoidVector.x) > Math.abs(avoidVector.y)) {
      velocity.x *= -1;
      x += avoidVector.x;
    } else {
      velocity.y *= -1;
      y += avoidVector.y;
    }
    matchAngleWithVelocity();
  }
  
  public void collide(PhysicalObject object, Vector2d avoidVector) {
    
    if (object instanceof Bullet) {
      damage(20);
    }
    
    if (bounceCountdown.isDone()) {
      bounce(avoidVector);
      bounceCountdown.restart();
    }
    
    super.collide(object, avoidVector);
  }
}
