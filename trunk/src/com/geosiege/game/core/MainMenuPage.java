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

import com.geosiege.common.ui.ClickHandler;
import com.geosiege.common.ui.SimpleButton;

public class MainMenuPage extends MenuPage {

  private SimpleButton gameButton;
  private SimpleButton helpButton;
  
  private static final float BUTTON_HEIGHT = 75;
  private static final float BUTTON_SPACING = 25;
  
  public MenuPage loadingPage;
  public MenuPage helpPage;
   
  public MainMenuPage(MenuPageSwitcher switcher) {
    super(switcher);
        
    float y = 210;
    gameButton = new SimpleButton(width / 2, y, 300, BUTTON_HEIGHT);
    gameButton.setText("Start Game");
    gameButton.setFontPaint(MainMenuGameMode.BUTTON_TEXT_PAINT);
    add(gameButton);
    
    y += BUTTON_HEIGHT + BUTTON_SPACING;
    helpButton = new SimpleButton(width / 2, y, 300, BUTTON_HEIGHT);
    helpButton.setText("How to Play");
    helpButton.setFontPaint(MainMenuGameMode.BUTTON_TEXT_PAINT);
    add(helpButton);
    
    gameButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick() {
        MainMenuPage.this.switcher.show(loadingPage);
      }
    });
    
    helpButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick() {
        MainMenuPage.this.switcher.show(helpPage);
      }
    });
  }
  
  public void draw(Canvas c) {
    super.draw(c);
    
    c.save();
    c.translate(x, y);
    
    c.drawText("Geo Siege", GameState.screenWidth / 2, 100, MainMenuGameMode.TITLE_PAINT);
    
    c.drawText(
        "Created by Scott Bailey - v0.1",
         GameState.screenWidth / 2,
         GameState.screenHeight - 30, MainMenuGameMode.MINOR_TEXT_PAINT);

    c.restore();
 
  }
}
