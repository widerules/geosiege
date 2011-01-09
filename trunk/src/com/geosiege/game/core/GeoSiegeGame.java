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
import android.view.MotionEvent;

import com.geosiege.game.effects.GeoEffects;
import com.geosiege.game.level.LevelLoader;
import com.geosiege.game.level.Stockpiles;
import com.geosiege.game.menu.MenuLevel;
import com.geosiege.game.storage.GameStorage;
import com.geosiege.game.storage.Preferences;
import com.zeddic.game.common.Game;
import com.zeddic.game.common.collision.CollisionManager;
import com.zeddic.game.common.effects.Effects;
import com.zeddic.game.common.ui.JoystickControl;
import com.zeddic.game.common.util.Countdown;

public class GeoSiegeGame extends Game {

  public static final int GAME_EVENT_WIN = 0;
  public static final int GAME_EVENT_DEAD = 1;
  public static final int GAME_EVENT_LOADED = 2;
  public static final int GAME_EVENT_PAUSED = 3;
  public static final int GAME_EVENT_STARTED = 4;
  
  private static final int GAME_STATE_SETUP = 0;
  private static final int GAME_STATE_PLAYING = 1;
  private static final int GAME_STATE_PROMPT = 2;
  private static final int GAME_STATE_PAUSED = 3;
  
  private static final long FIRST_SPAWN_DELAY = 0;
  
  private int gameState;
  private JoystickControl moveControls;
  private JoystickControl directionFireControls;
  private GameStatusBar statusBar = new GameStatusBar();
  
  private static final int MAX_WORLD_WIDTH = 2000;
  private static final int MAX_WORLD_HEIGHT = 2000;

  private MenuLevel menuLevel;
  private Countdown winCountdown = new Countdown(5000);
  private Countdown pauseButtonCooldown = new Countdown(100);
  
  public GeoSiegeGame(MenuLevel menuLevel) {
    this.menuLevel = menuLevel;
  }
  
  @Override
  public void init(int screenWidth, int screenHeight) {
    if (this.initialized)
      return;
    
    GameState.setScreen(screenWidth, screenHeight);
    
    // Setup the collision system.
    CollisionManager.setup(MAX_WORLD_WIDTH, MAX_WORLD_HEIGHT);
    CollisionManager.get().setCustomCollisionCheck(new GeoCustomCollisionCheck());

    // Populate the GameState
    GameState.player = new Player();
    GameState.stockpiles = new Stockpiles();
    GameState.effects = Effects.get();
    GameState.geoEffects = GeoEffects.get();
    GameState.camera = new Camera();
    
    // Create the enemies and reusable game objects. 
    GameState.stockpiles.populate();
    
    // Add UI elements
    setupUiElements();
    
    gameState = GAME_STATE_SETUP;
    
    loadLevel();
    
    initialized = true;
    triggerEvent(GAME_EVENT_LOADED);
  }
  
  private void setupUiElements() {
    
    // Compute possible x/y locations for the joysticks.
    float radius = JoystickControl.BORDER_RADIUS + 60;
    float left = radius;
    float right = GameState.screenWidth - radius;
    float top = radius;
    float bottom = GameState.screenHeight - radius;
    
    // Determine locations for the joysticks based on the location option.
    float moveX = left;
    float fireX = right;
    float moveY, fireY;
    String joystickLocation = GameStorage.preferences.getJoystickLocation();
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

    moveControls = new JoystickControl(moveX, moveY, true, Color.argb(255, 240, 240, 240));
    directionFireControls = new JoystickControl(fireX, fireY, true, Color.argb(255, 214, 28, 28));
    
    // Swap locations of move/fire if requested.
    if (GameStorage.preferences.getSwapJoysticks()) {
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
    GameState.stockpiles.reset();
    GameState.player.reset();
    GameState.effects.reset();
    winCountdown.reset();
    pauseButtonCooldown.restart();
  }
  
  public void loadLevel() {

    resetGameObjects();
    try {
      LevelLoader loader = new LevelLoader(GameStorage.scores, GameState.stockpiles.enemies, FIRST_SPAWN_DELAY);
      GameState.level = loader.loadLevel("levels/" + menuLevel.file);
      GameState.level.start();
    } catch (IOException e) {
      throw new RuntimeException("Unable load load level!", e);
    }
    GameState.player.spawnShipInMiddle();
    gameState = GAME_STATE_PLAYING;
    
    triggerEvent(GAME_EVENT_STARTED);
  }
  
  public void restart() {
    loadLevel();
  }
  
  public void pause() {
    super.pause();
    gameState = GAME_STATE_PAUSED;
    triggerEvent(GAME_EVENT_PAUSED);
  }
  
  public void resume() {
    super.resume();
    gameState = GAME_STATE_PLAYING;
  }
  
  @Override
  public void draw(Canvas c) {

    GameState.camera.apply(c);
    GameState.level.draw(c);
    GameState.stockpiles.draw(c);
    GameState.player.draw(c);
    GameState.effects.draw(c);
    GameState.camera.revert(c);
    
    if (gameState == GAME_STATE_PLAYING || gameState == GAME_STATE_PAUSED) {
      drawUiElements(c);
    }
  }
  
  @Override
  public void update(long time) {

    pauseButtonCooldown.update(time);
    
    if (gameState != GAME_STATE_PLAYING) {
      return;
    }
    
    GameStorage.stats.recordTimePlayed(time);
    
    GameState.level.update(time);
    GameState.stockpiles.update(time);
    GameState.player.update(time);
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
    
    if (GameState.player.gameOver) {
      endGame();
      return;
    }
    
    if (directionFireControls.isPressed()) {
      GameState.player.ship.fire(directionFireControls.getAngle());
    }
    moveControls.update(time);
    
    GameState.camera.ensureOnScreen(GameState.player.ship);
  }
  
  private void drawUiElements(Canvas c) {
    moveControls.draw(c);
    directionFireControls.draw(c);
    statusBar.draw(c);
  }
  
  //// USER INPUT
  
  /**
   * Handle movement.
   */
  public boolean onTouchEvent(MotionEvent e) {
    //if (GameState.player.ship.isDead())
    //  return true;
    
    moveControls.onTouchEvent(e);
    directionFireControls.onTouchEvent(e);
    
    synchronized (GameState.player.ship) {
      // Update the state of the ship based on the latest control input.
      GameState.player.ship.setAngle(moveControls.getAngle());
      GameState.player.ship.updateSpeed(
          moveControls.getXVelocity(),
          moveControls.getYVelocity());
    }

    try {
      Thread.sleep(16);
    } catch (InterruptedException ex) {}

    return true;
  }
  
  public boolean onTrackballEvent(MotionEvent e) {
    
    if (e.getAction() != MotionEvent.ACTION_DOWN || !pauseButtonCooldown.isDone())
      return true;
    
    this.gameState = gameState == GAME_STATE_PAUSED ? GAME_STATE_PLAYING : GAME_STATE_PAUSED;
    
    pauseButtonCooldown.restart();
    return true;
  }
}
