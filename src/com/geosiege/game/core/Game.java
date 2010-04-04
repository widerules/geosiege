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

import com.geosiege.common.Updater;

public class Game {
  
  ArcadeGameMode arcadeMode;
  MainMenuGameMode menuMode;
  Updater updater;

  boolean initalized = false;
  
  public Game(Updater updater) {
    this.updater = updater;
  }
  
  public void init() {
    if (initalized)
      return;
    
    arcadeMode = new ArcadeGameMode();
 
    menuMode = new MainMenuGameMode();
    menuMode.init();
    
    menuMode.arcadeMode = arcadeMode;
    
    updater.setMode(menuMode);
  }
  
  public void restartGame() {
    arcadeMode.init();
    updater.setMode(arcadeMode);
  }
  
  public void start() {
    updater.start();
  }
  
  public void stop() {
    updater.stop();
  }
  
  public void pause() {
    updater.pause();
  }
  
  /// USER INPUT
  /* public boolean onTouchEvent(MotionEvent e) {
    return updater.mode.onTouchEvent(e);
  }
  public boolean onTrackballEvent(MotionEvent e) {
    return updater.mode.onTrackballEvent(e);
  }
  public boolean onKeyDown(int key, KeyEvent e) {
    return updater.mode.onKeyDown(key, e);
  }
  public boolean onKeyUp(int key, KeyEvent e) {
    return updater.mode.onKeyUp(key, e);
  } */
}
