/*
 * Copyright (C) 2010 Geo Siege Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geosiege.game.core;

import android.graphics.Canvas;

import com.geosiege.game.highscore.Scorer;
import com.geosiege.game.ships.EnemyShip;
import com.geosiege.game.ships.PlayerShip;
import com.zeddic.game.common.GameObject;
import com.zeddic.game.common.util.Countdown;

public class Player extends GameObject {
  
  private static final int RESPAWN_DELAY = 1500;
  
  public int lives;
  public String livesString;
  public PlayerShip ship;
  public Scorer scorer;
  public boolean respawning;
  public boolean gameOver;
  
  private Countdown respawnDelay = new Countdown(RESPAWN_DELAY);
 
  public Player() {
    ship = new PlayerShip();
    scorer = new Scorer();
    reset();
  }
  
  public void reset() {
    ship.reset();
    scorer.reset();
    respawning = false;
    gameOver = false;
    setLives(3);
  }
  
  @Override
  public void draw(Canvas c) {
    if (ship.active) {
      ship.draw(c);
    }
  }
  
  @Override
  public void update(long time) {
    if (!ship.isDead()) {
      ship.update(time);
    } else if (!respawning && !gameOver) {
      handleDeath();
    }
    
    if (respawning) {
      respawnDelay.update(time);
      if (respawnDelay.isDone()) {
        respawnShip();
      }
    }
  }
  
  public void spawnShipInMiddle() {
    float x = GameState.level.map.left + GameState.level.map.width / 2;
    float y = GameState.level.map.top + GameState.level.map.height / 2;    
    ship.spawn(x, y);
    respawning = false;
  }
  
  public void respawnShip() { 
    ship.spawn(ship.x, ship.y);
    respawning = false;
  }
  
  private void handleDeath() {
    
    scorer.resetMultipliers();
    
    if (lives == 1) {
      setLives(0);
      gameOver = true;
      return;
    }
    
    respawning = true;
    respawnDelay.restart();
    setLives(lives - 1);
  }
  
  private void setLives(int lives) {
    this.lives = lives;
    this.livesString = Integer.toString(lives);
  }
  
  public void recordKill(Class<? extends EnemyShip> shipType) {
    scorer.recordKill(shipType);
  }
}
