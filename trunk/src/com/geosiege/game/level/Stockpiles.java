package com.geosiege.game.level;

import android.graphics.Canvas;

import com.geosiege.game.effects.TextFlash;
import com.geosiege.game.guns.Bullet;
import com.geosiege.game.guns.ShardBullet;
import com.geosiege.game.ships.Arrow;
import com.geosiege.game.ships.Blinker;
import com.geosiege.game.ships.DaBomb;
import com.geosiege.game.ships.DeathStar;
import com.geosiege.game.ships.EnemyShip;
import com.geosiege.game.ships.SimpleEnemyShip;
import com.zeddic.game.common.GameObject;
import com.zeddic.game.common.util.ObjectPoolManager;
import com.zeddic.game.common.util.ObjectStockpile;

public class Stockpiles extends GameObject {

  public ObjectStockpile enemies;
  public ObjectStockpile bullets;
  public ObjectStockpile ui;
  
  public Stockpiles() {
    enemies = new ObjectStockpile();
    bullets = new ObjectStockpile();
    ui = new ObjectStockpile();
  }
  
  public void populate() {
    enemies.createSupply(SimpleEnemyShip.class, 100);
    enemies.createSupply(DeathStar.class, 50);
    enemies.createSupply(DaBomb.class, 50);
    enemies.createSupply(Blinker.class, 50);
    enemies.createSupply(Arrow.class, 100);
    
    bullets.createSupply(Bullet.class, 300);
    bullets.createSupply(ShardBullet.class, 300);
    
    ui.createSupply(TextFlash.class, 10);
  }
  
  public void reset() {
    enemies.reset();
    bullets.reset();
    ui.reset();
  }
  
  @Override
  public void draw(Canvas c) {
    enemies.draw(c);
    bullets.draw(c);
    ui.draw(c);
  }
  
  @Override
  public void update(long time) {
    enemies.update(time);
    bullets.update(time);
    ui.update(time);
  }
  
  public void killAllEnemies() {
    EnemyShip ship;
    for (ObjectPoolManager<? extends GameObject> set : enemies.supply.values()) {
      for (int i = 0 ; i < set.pool.items.length; i++) {
        ship = ((EnemyShip) set.pool.items[i]);
        if (ship.active) {
          ship.die();
        }
      }
    }
  }
}
