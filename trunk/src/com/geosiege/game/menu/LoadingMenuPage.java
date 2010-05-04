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

package com.geosiege.game.menu;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LoadingMenuPage extends MenuPage {
  private static final int LOADING_DELAY = 500;
  
  public MenuPage mainPage;
  private MainMenuGameMode gameMode;
  private Paint loadingTextPaint;
  
  
  private int delayCount = 0;
  
  public LoadingMenuPage(MenuPageSwitcher switcher, MainMenuGameMode gameMode) {
    super(switcher);
    this.gameMode = gameMode;
    
    loadingTextPaint = new Paint(MainMenuGameMode.TITLE_PAINT);
    loadingTextPaint.setTextSize(50);
  }
  
  public void draw(Canvas c) {
    super.draw(c);
    
    c.save();
    c.translate(x, y);
    
    c.drawText("Loading", width / 2, height / 2 + 20, loadingTextPaint);
    
    c.restore();
  }
  
  public void reset() {
    delayCount = 0;
  }
  
  public void update(long time) {
    delayCount += time;
    if (delayCount > LOADING_DELAY) {
      gameMode.switchToGame();
    }
  }
}
