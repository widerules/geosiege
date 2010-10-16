package com.geosiege.game.ships;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.geosiege.game.components.MapBoundsComponent;
import com.geosiege.game.core.GameState;
import com.geosiege.game.effects.TeleportExplosion;
import com.geosiege.game.guns.Arsenal;
import com.geosiege.game.guns.Gun;
import com.geosiege.game.guns.control.AimingGunControl;
import com.geosiege.game.util.DistanceUtil;
import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.collision.CollisionComponent;
import com.zeddic.game.common.collision.CollisionManager;
import com.zeddic.game.common.particle.Particle;
import com.zeddic.game.common.transistions.Transitions;
import com.zeddic.game.common.util.Bounds;
import com.zeddic.game.common.util.Circle;
import com.zeddic.game.common.util.ComponentManager;
import com.zeddic.game.common.util.Polygon;
import com.zeddic.game.common.util.RandomUtil;
import com.zeddic.game.common.util.Vector2d;
import com.zeddic.game.common.util.Polygon.PolygonBuilder;

public class Blinker extends EnemyShip {

  ////SETUP OBJECT SHAPE AND PAINT
  private static final Paint PAINT;
  private static final Polygon SHAPE;
  private static final float ANGLE_OFFSET = -45;
  static {
    PAINT = new Paint();
    PAINT.setColor(Color.argb(255, 255, 204, 0));
    PAINT.setStyle(Paint.Style.STROKE);
    PAINT.setStrokeWidth(2);
    
    SHAPE = new PolygonBuilder()
      .add(-13, 0)
      .add(-4, 4)
      .add(0, 13)
      .add(4, 4)
      .add(13, 0)
      .add(4, -4)
      .add(0, -13)
      .add(-4, -4)
      .build();
  }
  
  private static final int PARTS_TO_RECIEVE = TeleportExplosion.NUMBER_OF_SHARDS;
  private static final int TELEPORT_RANGE = 150;
  
  private boolean teleporting = false;
  private int recievedParts = 0;
  private ComponentManager components;
  private Gun gun;
  
  public Blinker() {
    this(0, 0);
  }
  
  public Blinker(float x, float y) {
    super(x, y);
  
    this.exp = 20;
    this.paint = PAINT;
    this.bounds = new Bounds(new Circle(11));
    this.setAngle(45);
    
    gun = Arsenal.getSniper(this);
    gun.setGunControl(new AimingGunControl(this, GameState.player.ship, 1000, 0));
    gun.setAutoFire(true);
    
    components = new ComponentManager(this);
    components.add(new CollisionComponent(this, CollisionManager.TYPE_HIT_RECEIVE));
    components.add(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_COLLIDE));
    
    reset();
  }
  
  @Override
  public void reset() {
    super.reset(); 
    teleporting = false;
    recievedParts = 0;
  }
  
  @Override
  public void update(long time) {
    super.update(time);
    if (!isSpawning())
      components.update(time);
    
    if (teleporting) {
      return;
    }
    
    gun.update(time);
    
    if (inRangeOfPlayer()) {
      startTeleport();
    }
  }
  
  private void startTeleport() {
    GameState.geoEffects.teleport(x, y, this);
    x = RandomUtil.nextFloat(GameState.level.map.spawnLeft, GameState.level.map.spawnRight);
    y = RandomUtil.nextFloat(GameState.level.map.spawnTop, GameState.level.map.spawnBottom);
    recievedParts = 0;
    teleporting = true;
  }
  
  private void endTeleport() {
    teleporting = false;
  }
  
  private boolean inRangeOfPlayer() {
    return DistanceUtil.objectsWithinRange(this, GameState.player.ship, TELEPORT_RANGE);
  }
  
  @Override
  protected float getAngleOffset() {
    return ANGLE_OFFSET;
  }
  
  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    
    if (teleporting) {
      double progress = Transitions.getProgress(Transitions.LINEAR, getTeleportProgress());
      paint.setAlpha((int) (255 * progress));
      scale = (float) progress; 
    } else {
      scale = 1;
    }
    
    canvas.save();
    canvas.translate(x, y);
    canvas.rotate(getAngleOffset() + angle);
    canvas.scale(scale, scale);
    canvas.drawPath(SHAPE.path, paint);
    canvas.restore();

    components.draw(canvas);
  }
  
  private double getTeleportProgress() {
    return (double) recievedParts / (double) PARTS_TO_RECIEVE;
  }
  
  public void collide(PhysicalObject object, Vector2d avoidVector) {
    if (object instanceof Particle) {
      recievedParts++;
      if (recievedParts >= PARTS_TO_RECIEVE) {
        endTeleport();
      }
    } else if (!teleporting) {
      super.collide(object, avoidVector);
    }
  }
}
