package com.geosiege.game.level;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Canvas;

import com.geosiege.common.GameObject;
import com.geosiege.common.util.ObjectPoolManager;
import com.geosiege.game.ships.EnemyShip;

public class EnemyStockpile extends GameObject {
  
  private Map<Class<? extends EnemyShip>, ObjectPoolManager<? extends EnemyShip>> supply;
    
  public EnemyStockpile() {
    supply = new HashMap<Class<? extends EnemyShip>, ObjectPoolManager<? extends EnemyShip>>();
  }
 
  public <T extends EnemyShip> ObjectPoolManager<T> createSupply(
      Class<T> shipType, int maxShips) {
    
    ObjectPoolManager<T> pool = new ObjectPoolManager<T>(shipType, maxShips);
    supply.put(shipType, pool);
    return pool;
  }
  
  public void update(long time) {
    for (ObjectPoolManager<? extends EnemyShip> pool : supply.values()) {
      pool.update(time);
    }
  }
  
  public void draw(Canvas canvas) {
    for (ObjectPoolManager<? extends EnemyShip> pool : supply.values()) {
      pool.draw(canvas);
    }
  }
  
  @SuppressWarnings("unchecked")
  public <T extends EnemyShip> ObjectPoolManager<T> getSupply(Class<T> shipType) {
    return (ObjectPoolManager<T>) supply.get(shipType);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends EnemyShip> T take(Class<T> shipType) {
    return (T) supply.get(shipType).take();
  }
}
