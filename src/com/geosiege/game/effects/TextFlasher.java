package com.geosiege.game.effects;

import com.geosiege.game.core.GameState;

public class TextFlasher {
  
  public void flashMessage(String message) {
    flashMessage(message, 0);
  }
  
  public void flashMessage(String message, float offset) {
    TextFlash flash = GameState.stockpiles.ui.take(TextFlash.class);
    if (flash == null) {
      return;
    }
    
    flash.flashMessage(GameState.player.ship.x, GameState.player.ship.y + offset, message);
  } 
}
