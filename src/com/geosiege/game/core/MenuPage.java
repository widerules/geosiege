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

import com.geosiege.common.ui.Container;

public class MenuPage extends Container {

  MenuPageSwitcher switcher;
  
  public MenuPage(MenuPageSwitcher switcher) {
    this.switcher = switcher;
    this.width = GameState.screenWidth;
    this.height = GameState.screenHeight;
  }
 
  public void reset() {

  }
  
  /*public void draw(Canvas canvas) {
    
  }
  
  public void update(long time) {
    
  } */
}
