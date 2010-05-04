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
import android.view.MotionEvent;

import com.geosiege.common.animation.Transition;
import com.geosiege.common.animation.Transitions;
import com.geosiege.game.core.GameState;

public class MenuPageSwitcher {

  private static final int TRANSITION_TIME = 500;
  
  MenuPage alpha;
  MenuPage beta;
  MenuPage entering;
  MenuPage leaving;
  
  Transition slideInTransitionIncoming;
  Transition slideInTransitionLeaving;
  Transition slideOutTransitionIncoming;
  Transition slideOutTransitionLeaving;
  
  Transition incomingTransition;
  Transition leavingTransition;
  
  boolean moving;
  
  public MenuPageSwitcher() {
    alpha = null;
    beta = null;
    entering = beta;
    moving = false;
    
    slideInTransitionIncoming = new Transition((float) GameState.screenWidth, 0,
        TRANSITION_TIME, Transitions.EASE_IN_OUT);
    slideInTransitionLeaving = new Transition(0, (float) -GameState.screenWidth,
        TRANSITION_TIME, Transitions.EASE_IN_OUT);
    
    
    slideOutTransitionIncoming = new Transition((float) -GameState.screenWidth, 0,
        TRANSITION_TIME, Transitions.EASE_IN_OUT);
    slideOutTransitionLeaving = new Transition(0, (float) GameState.screenWidth,
        TRANSITION_TIME, Transitions.EASE_IN_OUT);
    
  }
  
  public void show(MenuPage page) {
    show(page, true);
  }
  
  public void show(MenuPage page, boolean incoming) {
    
    if (page == entering) {
      return;
    }
    
    page.reset();
    
    if (entering == beta) {
      alpha = page;
      entering = alpha;
      leaving = beta;
    } else {
      beta = page;
      entering = beta;
      leaving = alpha;
    }
    
    if (incoming) {
      incomingTransition = slideInTransitionIncoming;
      leavingTransition = slideInTransitionLeaving;
    } else {
      incomingTransition = slideOutTransitionIncoming;
      leavingTransition = slideOutTransitionLeaving;
    }
       
    incomingTransition.reset();
    leavingTransition.reset();
    
    updatePagePositions();
    
    moving = true;
    
  }
  
  private void updatePagePositions() {
    if (entering != null)
      entering.x = incomingTransition.get();
    if (leaving != null)
      leaving.x = leavingTransition.get();
  
  }
  
  public void update(long time) {
    if (moving) {
      incomingTransition.update(time);
      leavingTransition.update(time);
      
      updatePagePositions();
      
      if (incomingTransition.finished) {
        moving = false;
      }
    }
    
    if (entering != null)
      entering.update(time);
    //if (beta != null)
    //  beta.update(time);
  }
  
  public void draw(Canvas c) {
    if (alpha != null)
      alpha.draw(c);
    if (beta != null)
      beta.draw(c);
  }
  
  public boolean onTouchEvent(MotionEvent e) {
    if (entering != null)
      entering.onTouchEvent(e);
    return true;
  }
  
  public MenuPage getActive() {
    return entering;
  }
  
}
