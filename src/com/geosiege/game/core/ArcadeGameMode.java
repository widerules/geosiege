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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.geosiege.common.GameMode;
import com.geosiege.common.collision.CollisionManager;
import com.geosiege.common.explosion.ExplosionManager;
import com.geosiege.common.ui.JoystickControl;
import com.geosiege.common.ui.ProgressBar;
import com.geosiege.common.ui.TrackballControl;
import com.geosiege.game.resources.GameResources;
import com.geosiege.game.ships.PlayerShip;

public class ArcadeGameMode extends GameMode {
  
  ProgressBar healthBar;
  JoystickControl moveControls;
  JoystickControl directionFireControls;
  TrackballControl fireControls;
  
  
  String pausedString = "Resume";
  String runningString = "Pause";
  Paint textPaint;
  
  boolean pause = false;
  boolean initalized = false;
  
  public void init() {
    
    textPaint = new Paint();
    textPaint.setTextSize(30);
    textPaint.setStrikeThruText(false);
    textPaint.setUnderlineText(false);
    textPaint.setTypeface(GameResources.font);
    textPaint.setTextAlign(Align.RIGHT);
    textPaint.setColor(Color.WHITE);
    
    // Setup the world.
    GameState.map = new Map();
    CollisionManager.setup(GameState.map.width, GameState.map.height);
    CollisionManager.get().setCustomCollisionCheck(new GeoCustomCollisionCheck());
    
    // Populate it
    GameState.player = new Player();
    GameState.playerShip = new PlayerShip(300, 300);
    GameState.playerShip.gameMode = this;
    GameState.map.popuplate();
    GameState.explosionManager = ExplosionManager.get();
    
    // Create a camera to look at the world
    GameState.camera = new Camera(GameState.playerShip.x, GameState.playerShip.y);
    
    // Add UI elements
    healthBar = new ProgressBar(GameState.screenWidth - 35, 40, 80, 100);
    moveControls = new JoystickControl(
        JoystickControl.BORDER_RADIUS,
        JoystickControl.BORDER_RADIUS);
    directionFireControls = new JoystickControl(
        GameState.screenWidth - JoystickControl.BORDER_RADIUS,
        GameState.screenHeight - JoystickControl.BORDER_RADIUS);
    fireControls = new TrackballControl();
    initalized = true;
  }
  
  public void endGame() {
    updater.handler.sendEmptyMessage(1);
    int z = 5;
  }
  
  @Override
  public void draw(Canvas c) {
    GameState.camera.apply(c);
    
    GameState.map.draw(c);
    GameState.playerShip.draw(c);
    GameState.explosionManager.draw(c);
    
    GameState.camera.revert(c);
   
    moveControls.draw(c);
    directionFireControls.draw(c);
    
    c.drawText(GameState.player.experienceString, GameState.screenWidth - 30, 30, textPaint);
    
    healthBar.value = GameState.playerShip.health;
    healthBar.max = GameState.player.maxHealth;
    healthBar.draw(c);
  }
  
  @Override
  public void update(long time) {
    //if (pause) 
    //  return;
    
    GameState.map.update(time);
    GameState.playerShip.update(time);
    GameState.explosionManager.update(time);
    
    /*if(fireControls.shouldFire()) {
      GameState.playerShip.fire();
    } */
    
    if (directionFireControls.isPressed()) {
      GameState.playerShip.fire(directionFireControls.getAngle());
    }
    
    moveControls.update(time);
    
    GameState.camera.ensureOnScreen(GameState.playerShip.x, GameState.playerShip.y);
  }
  
  //// USER INPUT
  
  /**
   * Handle movement.
   */
  public boolean onTouchEvent(MotionEvent e) {
    synchronized (moveControls) {
      moveControls.onTouchEvent(e);
    }
    
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
      pause = !pause;
    }
 
    try {
      Thread.sleep(16);
      } catch (InterruptedException ex) {
    }

    return true;
  }
  
  /**
   * Handle trackball / firing.
   */
  public boolean onTrackballEvent(MotionEvent e) {
    return fireControls.onTrackballEvent(e);
  }
  
  public boolean onKeyDown(int key, KeyEvent e) {
    return fireControls.onKeyDown(key, e);
  }
  
  public boolean onKeyUp(int key, KeyEvent e) {
    return fireControls.onKeyUp(key, e);
  }
}
