package com.geosiege.game.effects;

import com.zeddic.game.common.GameObject;
import com.zeddic.game.common.util.Countdown;

public class CountdownEffect extends GameObject {

  private static final long ONE_SECOND = 1000;

  private Countdown incrementCountdown;
  private int maxSeconds;
  private int seconds;
  private TextFlasher flasher;
  private boolean counting;
  
  public CountdownEffect(int seconds, TextFlasher flasher) {
    this.flasher = flasher;
    incrementCountdown = new Countdown(ONE_SECOND);
    maxSeconds = seconds;
    this.seconds = maxSeconds;
  }
  
  @Override
  public void reset() {
    counting = false;
    seconds = maxSeconds;
  }
  
  private void flash() {
    flasher.flashMessage(Integer.toString(seconds), -20);
  }
  
  public void start() {
    flash();
    counting = true;
    incrementCountdown.start();
  }
  
  public boolean isDone() {
    return seconds == 0;
  }
  
  @Override
  public void update(long time) {

    if (!counting) {
      return;
    }

    incrementCountdown.update(time);
    if (incrementCountdown.isDone()) {

      seconds--;
      
      if (seconds == 0) {
        counting = false;
        return;
      }
      
      flash();
      incrementCountdown.restart();
    }
  }
}
