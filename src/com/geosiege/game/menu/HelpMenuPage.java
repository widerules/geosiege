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

import com.geosiege.common.ui.ClickHandler;
import com.geosiege.common.ui.Image;
import com.geosiege.game.core.GameState;
import com.geosiege.game.resources.GameResources;

public class HelpMenuPage extends MenuPage {

  public MainMenuPage mainPage;
  
  private Image helpImage;
  
  public HelpMenuPage(MenuPageSwitcher switcher) {
    super(switcher);
    
    helpImage = new Image(GameResources.controls, 0, 0,
        GameState.screenWidth, GameState.screenHeight);
    add(helpImage);
    
    helpImage.addClickHandler(new ClickHandler() {
      @Override
      public void onClick() {
        HelpMenuPage.this.switcher.show(mainPage, false);
      }
    });
  }
  
  public boolean onBackPress() {
    switcher.show(mainPage, false);
    return true;
  }
}
