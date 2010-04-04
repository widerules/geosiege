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
import android.view.MotionEvent;

import com.geosiege.common.GameMode;
import com.geosiege.game.resources.GameResources;

public class MainMenuGameMode extends GameMode {
 
  
  public static final Paint TITLE_PAINT;
  public static final Paint BUTTON_TEXT_PAINT;
  public static final Paint MINOR_TEXT_PAINT;
  
  static {
    TITLE_PAINT = new Paint();
    TITLE_PAINT.setTextSize(70);
    TITLE_PAINT.setStrikeThruText(false);
    TITLE_PAINT.setUnderlineText(false);
    TITLE_PAINT.setTypeface(GameResources.font);
    TITLE_PAINT.setTextAlign(Align.CENTER);
    TITLE_PAINT.setColor(Color.WHITE);
    
    BUTTON_TEXT_PAINT = new Paint(TITLE_PAINT);
    BUTTON_TEXT_PAINT.setColor(Color.BLUE);
    BUTTON_TEXT_PAINT.setTextSize(30);
    
    MINOR_TEXT_PAINT = new Paint(TITLE_PAINT);
    MINOR_TEXT_PAINT.setTextSize(22);
  }
  
  public ArcadeGameMode arcadeMode;
  
  MainMenuPage mainPage;
  LoadingMenuPage loadingPage;
  HelpMenuPage helpPage;
  
  MenuPageSwitcher switcher;
  RotatingBackgroundLayer background;
  
  
  
  public MainMenuGameMode() {
    background = new RotatingBackgroundLayer();
    
    switcher = new MenuPageSwitcher();
    
    mainPage = new MainMenuPage(switcher);
    loadingPage = new LoadingMenuPage(switcher, this);
    helpPage = new HelpMenuPage(switcher);
    
    mainPage.loadingPage = loadingPage;
    mainPage.helpPage = helpPage;
    loadingPage.mainPage = mainPage;
    helpPage.mainPage = mainPage;
    
    switcher.show(mainPage);
  }
  
  public void switchToGame() {
    arcadeMode.init();
    updater.setMode(arcadeMode);
  }
  
  public void init() {
   
  }
  
  /*public void gotoPage(PageType pageType) {
    MenuPage page = getPageFromType(pageType);
    switcher.show(page);
  }
  
  public void returnToPage(PageType pageType) {
    
  }
  
  private MenuPage getPageFromType(PageType pageType) {
    switch (pageType) {
      case MAIN: return mainPage;
      case LOADING: return loadingPage;
      default: return mainPage;
    }
  } */
  
  @Override
  public void draw(Canvas c) {
    background.draw(c);
    
    switcher.draw(c);
    /*mainPage.draw(c);
    loadingPage.draw(c); */
  }
  
  @Override
  public void update(long time) {
    background.update(time);
    
    switcher.update(time);
    
    /*mainPage.update(time);
     loadingPage.update(time);*/
  }
  
  /**
   * Handle movement.
   */
  public boolean onTouchEvent(MotionEvent e) {
    switcher.onTouchEvent(e);
    return true;
  }
}
