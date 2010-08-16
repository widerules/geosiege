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

import java.io.IOException;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

import com.geosiege.common.Game;
import com.geosiege.common.collision.CollisionManager;
import com.geosiege.common.effects.Effects;
import com.geosiege.common.ui.JoystickControl;
import com.geosiege.common.ui.ProgressBar;
import com.geosiege.common.util.Countdown;
import com.geosiege.game.MenuLevel;
import com.geosiege.game.Preferences;
import com.geosiege.game.level.EnemyStockpile;
import com.geosiege.game.level.LevelLoader;
import com.geosiege.game.resources.GameResources;
import com.geosiege.game.ships.DeathStar;
import com.geosiege.game.ships.PlayerShip;
import com.geosiege.game.ships.SimpleEnemyShip;

public class GeoSiegeGame extends Game {

  public static final int GAME_EVENT_WIN = 0;
  public static final int GAME_EVENT_DEAD = 1;
  public static final int GAME_EVENT_LOADED = 2;
  public static final int GAME_EVENT_PAUSED = 3;
  
  private static final int GAME_STATE_SETUP = 0;
  private static final int GAME_STATE_PLAYING = 1;
  private static final int GAME_STATE_PROMPT = 2;
  private static final int GAME_STATE_PAUSED = 3;
  
  private static final long FIRST_SPAWN_DELAY = 3000;
  
  private int gameState;
  private ProgressBar healthBar;
  private JoystickControl moveControls;
  private JoystickControl directionFireControls;
  
  // private static final String PAUSED_STRING = "Resume";
  // private static final String PAUSE_STRING = "Pause";
  private static final int MAX_WORLD_WIDTH = 2000;
  private static final int MAX_WORLD_HEIGHT = 2000;
  private static final Paint TEXT_PAINT;
  static {
    TEXT_PAINT = new Paint();
    TEXT_PAINT.setTextSize(30);
    TEXT_PAINT.setStrikeThruText(false);
    TEXT_PAINT.setUnderlineText(false);
    TEXT_PAINT.setTypeface(GameResources.font);
    TEXT_PAINT.setTextAlign(Align.RIGHT);
    TEXT_PAINT.setColor(Color.WHITE);
  }
  
  private MenuLevel menuLevel;
  private Countdown winCountdown;

  public GeoSiegeGame(MenuLevel menuLevel) {
    this.menuLevel = menuLevel;
  }
  
  @Override
  public void init() {
    if (this.initialized)
      return;
    
    winCountdown = new Countdown(5000);
    
    // Setup the collision system.
    CollisionManager.setup(MAX_WORLD_WIDTH, MAX_WORLD_HEIGHT);
    CollisionManager.get().setCustomCollisionCheck(new GeoCustomCollisionCheck());

    // Populate the GameState
    GameState.player = new Player();
    GameState.enemyStockpile = new EnemyStockpile();
    GameState.playerShip = new PlayerShip();
    GameState.effects = Effects.get();
    GameState.camera = new Camera();
    
    // Create the enemies. 
    populateEnemyStockpile(GameState.enemyStockpile);
    
    // Add UI elements
    setupUiElements();
    
    gameState = GAME_STATE_SETUP;
    
    loadLevel();
    
    initialized = true;
    triggerEvent(GAME_EVENT_LOADED);
  }
  
  private void populateEnemyStockpile(EnemyStockpile enemyStockpile) {
    enemyStockpile.createSupply(SimpleEnemyShip.class, 100);
    enemyStockpile.createSupply(DeathStar.class, 50);
  }
  
  private void setupUiElements() {
    healthBar = new ProgressBar(GameState.screenWidth - 35, 40, 80, 100);
    
    // Compute possible x/y locations for the joysticks.
    float radius = JoystickControl.BORDER_RADIUS;
    float left = radius;
    float right = GameState.screenWidth - radius;
    float top = radius;
    float bottom = GameState.screenHeight - radius;
    
    // Determine locations for the joysticks based on the location option.
    float moveX = left;
    float fireX = right;
    float moveY, fireY;
    String joystickLocation = GameState.preferences.getJoystickLocation();
    if (joystickLocation.equals(Preferences.JOYSTICK_LOCATION_TOPLEFT_BOTTOMRIGHT)) {
      moveY = top;
      fireY = bottom;
    } else if (joystickLocation.equals(Preferences.JOYSTICK_LOCATION_BOTTOMLEFT_TOPRIGHT)) {
      moveY = bottom;
      fireY = top;
    } else {
      moveY = bottom;
      fireY = bottom;
    }

    moveControls = new JoystickControl(moveX, moveY);
    directionFireControls = new JoystickControl(fireX, fireY);
    
    // Swap locations of move/fire if requested.
    if (GameState.preferences.getSwapJoysticks()) {
      JoystickControl temp = moveControls;
      moveControls = directionFireControls;
      directionFireControls = temp;
    }
  }
  
  private void endGame() {
    gameState = GAME_STATE_PROMPT;
    updater.triggerEventHandler(GAME_EVENT_DEAD);
  }
  
  private void winGame() {
    gameState = GAME_STATE_PROMPT;
    updater.triggerEventHandler(GAME_EVENT_WIN);
  }
  
  private void resetGameObjects() {
    GameState.enemyStockpile.reset();
    GameState.player.reset();
    GameState.playerShip.reset();
    GameState.effects.reset();
    winCountdown.reset();
  }
  
  public void loadLevel() {

    resetGameObjects();
    try {
      LevelLoader loader = new LevelLoader(GameState.enemyStockpile, FIRST_SPAWN_DELAY);
      GameState.level = loader.loadLevel("levels/" + menuLevel.file);
    } catch (IOException e) {
      throw new RuntimeException("Unable load load level!", e);
    }
    GameState.playerShip.centerOnMap();
    gameState = GAME_STATE_PLAYING;
  }
  
  public void restart() {
    loadLevel();
  }
  
  public void pause() {
    gameState = GAME_STATE_PAUSED;
    triggerEvent(GAME_EVENT_PAUSED);
  }
  
  public void resume() {
    gameState = GAME_STATE_PLAYING;
  }
  
  @Override
  public void draw(Canvas c) {

    GameState.camera.apply(c);
    GameState.level.draw(c);
    GameState.enemyStockpile.draw(c);
    GameState.playerShip.draw(c);
    GameState.effects.draw(c);
    GameState.camera.revert(c);
    
    if (gameState == GAME_STATE_PLAYING || gameState == GAME_STATE_PAUSED) {
      drawUiElements(c);
    }
  }
  
  @Override
  public void update(long time) {

    if (gameState != GAME_STATE_PLAYING) {
      return;
    }
    
    GameState.stats.recordTimePlayed(time);
    
    GameState.level.update(time);
    GameState.enemyStockpile.update(time);
    GameState.playerShip.update(time);
    GameState.effects.update(time);
    
    if (GameState.level.complete) {
      // After the player wins, give them a few seconds to pickup any 
      // remaining points before ending.
      if (!winCountdown.isCounting()) {
        winCountdown.start();
      }
      winCountdown.update(time);
      if (winCountdown.isDone()) {
        winGame();
        return;
      }
    }
    
    if (GameState.playerShip.isDead()) {
      endGame();
      return;
    }
    
    if (GameState.playerShip.isDying()) {
      return;
    }
    
    if (directionFireControls.isPressed()) {
      GameState.playerShip.fire(directionFireControls.getAngle());
    }
    moveControls.update(time);
    
    GameState.camera.ensureOnScreen(GameState.playerShip);
  }
  
  private void drawUiElements(Canvas c) {
    moveControls.draw(c);
    directionFireControls.draw(c);
    
    c.drawText(GameState.player.experienceString, GameState.screenWidth - 30, 30, TEXT_PAINT);
    
    healthBar.value = GameState.playerShip.health;
    healthBar.max = GameState.player.maxHealth;
    healthBar.draw(c);
  }
  
  //// USER INPUT
  
  /**
   * Handle movement.
   */
  public boolean onTouchEvent(MotionEvent e) {
    if (GameState.playerShip.isDead())
      return true;
    
    moveControls.onTouchEvent(e);
    directionFireControls.onTouchEvent(e);
    
    synchronized (GameState.playerShip) {
      // Update the state of the ship based on the latest control input.
      GameState.playerShip.setAngle(moveControls.getAngle());
      GameState.playerShip.setVelocity(
          moveControls.getXVelocity() * 100,
          moveControls.getYVelocity() * 100);
    }
    
    float x = e.getX();
    float y = e.getY();
    if ( x > GameState.screenWidth - 100 &&
         y < GameState.screenHeight - 50) {
      gameState = (gameState == GAME_STATE_PAUSED ? GAME_STATE_PLAYING : GAME_STATE_PAUSED);
    } 

    //try {
    //  Thread.sleep(16);
    //} catch (InterruptedException ex) {}

    return true;
  }
  
  @Override
  public void onFocusChangedEvent(boolean hasFocus) {
    if (initialized) {
      if (!hasFocus) {
        sleep();
        pause();
      } else {
        wakeup();
      }
    }
  }
  
  @Override
  public void onLayoutChangedEvent(boolean changed, int left, int top, int right, int bottom) {
    GameState.setScreen(right - left, bottom - top);
  }
}
