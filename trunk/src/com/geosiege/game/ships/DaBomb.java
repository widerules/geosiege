package com.geosiege.game.ships;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.geosiege.game.components.MapBoundsComponent;
import com.geosiege.game.components.SimplePathComponent;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Gun;
import com.zeddic.game.common.collision.CollisionComponent;
import com.zeddic.game.common.collision.CollisionManager;
import com.zeddic.game.common.transistions.Transitions;
import com.zeddic.game.common.util.Bounds;
import com.zeddic.game.common.util.Circle;
import com.zeddic.game.common.util.ComponentManager;
import com.zeddic.game.common.util.Countdown;

public class DaBomb extends EnemyShip {

  ////SETUP OBJECT SHAPE AND PAINT
  private static final Paint PAINT;
  private static final Paint ARMING_PAINT;
  private static final Circle SHAPE;
  private static final float RADIUS = 8;
  static {
    PAINT = new Paint();
    PAINT.setColor(Color.argb(255, 255, 0, 234));
    PAINT.setStyle(Paint.Style.STROKE);
    PAINT.setStrokeWidth(2);
    
    ARMING_PAINT = new Paint();
    ARMING_PAINT.setColor(Color.argb(255, 255, 0, 30));
    ARMING_PAINT.setStyle(Paint.Style.FILL);
    
    SHAPE = new Circle(RADIUS);
    SHAPE.path.moveTo(0, -RADIUS);
    SHAPE.path.lineTo(0, RADIUS);
    SHAPE.path.moveTo(-RADIUS, 0);
    SHAPE.path.lineTo(RADIUS, 0);
  }
  
  private static final int BULLETS_IN_BOMB = 50;
  private static final float SPEED = 45;
  private static final float WAIT_DISTANCE = 90;
  private static final float JITTER = 50;
  private static final long TIME_TO_DETONATE = 2000;
  private static final long TIME_BETWEEN_FLASHES = 500;
  private static final long TIME_BETWEEN_FLASHES_DECREMENT = 100;
  private static final long MINIMUM_FLASH_DURATION = 100;
  
  private ComponentManager components;
  private SimplePathComponent pather;
  private Gun bombGun;
  private Countdown blowupCountdown = new Countdown(TIME_TO_DETONATE);
  private Countdown flashCountdown = new Countdown(TIME_BETWEEN_FLASHES);
  private boolean arming;
  
  public DaBomb() {
    this(0, 0);
  }
  
  public DaBomb(float x, float y) {
    super(x, y);
  
    this.exp = 20;
    this.paint = PAINT;
    this.bounds = new Bounds(SHAPE);
    this.setAngle(45);
    
    pather = new SimplePathComponent(this, GameState.player.ship, WAIT_DISTANCE, SPEED, JITTER);
    bombGun = Arsenal.getBombBurst(this);
    bombGun.setClipSize(BULLETS_IN_BOMB);
    
    components = new ComponentManager(this);
    components.add(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    components.add(pather);
    components.add(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE));
    
    reset();
  }
  
  @Override
  public void reset() {
    super.reset();
    arming = false;
    pather.reset();
    blowupCountdown.reset();
    flashCountdown.reset(TIME_BETWEEN_FLASHES);
  }
  
  @Override
  public void update(long time) {
    super.update(time);
    if (!isSpawning())
      components.update(time);
    
    
    if (!arming && pather.inWaitRange()) {
      armBomb();
    }
    
    blowupCountdown.update(time);
    if (blowupCountdown.isDone()) {
      detonate();
    }
    flashCountdown.update(time);
    if (flashCountdown.isDone()) {
      long newFlashDuration = Math.max(
          MINIMUM_FLASH_DURATION,
          flashCountdown.duration - TIME_BETWEEN_FLASHES_DECREMENT);
      flashCountdown.reset(newFlashDuration);
      flashCountdown.start();
    }
  }
  
  @Override
  public void draw(Canvas canvas) {
    
    super.draw(canvas);
    
    canvas.save();
    canvas.translate(x, y);
    canvas.rotate(getAngleOffset() + angle);
    canvas.scale(scale, scale);
    
    // If arming, fill in the middle with a flashing countdown.
    if (arming) {
      double progress = Transitions.getProgress(Transitions.LINEAR, flashCountdown.getProgress());
      ARMING_PAINT.setAlpha((int) (255 * progress));
      canvas.drawPath(SHAPE.path, ARMING_PAINT);
    }
    canvas.drawPath(SHAPE.path, paint);
    canvas.restore();
    
    components.draw(canvas);
  }
  
  private void armBomb() {
    arming = true;
    pather.disable();
    blowupCountdown.start();
    flashCountdown.start();
  }
  
  private void detonate() {
    bombGun.fire();
    kill();
  }
}
