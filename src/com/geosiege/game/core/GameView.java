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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.geosiege.common.Updater;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

  private Updater updater;
  private Game game;
  private SurfaceHolder holder;
  private Context context;
  
  public GameView(Context context, AttributeSet attrs) {
    super(context, attrs);

    EndGameDialogHandler handler = new EndGameDialogHandler();
    
    GameState.context = context;
    
    this.context = context;
    this.setFocusableInTouchMode(true);
    this.holder = getHolder();
    this.holder.addCallback(this);
    this.updater = new Updater(holder);
    this.updater.handler = handler;
    this.game = new Game(updater);
  }

  /**
   * Standard window-focus override. Notice focus lost so we can pause on
   * focus lost. e.g. user switches to take a call.
   */
  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    if (game.initalized) {
      if (!hasFocus) {
        game.pause();
      } else {
        game.start();
      }
    }
  }

  @Override
  public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    GameState.setScreen(right - left, bottom - top);
  }
  
  /**
   * We are able to 
   */
  public void surfaceCreated(SurfaceHolder holder) {
    game.init();
    game.start();
  }
  
  /**
   * Surface must *NOT* be used after this call.
   */
  public void surfaceDestroyed(SurfaceHolder holder) {
    game.stop();
  }
  
  @Override
  public boolean onTouchEvent(MotionEvent e) {
    return updater.onTouchEvent(e);
  }
  
  @Override
  public boolean onTrackballEvent(MotionEvent e) {
    return updater.onTrackballEvent(e);
  }

  @Override
  public boolean onKeyDown(int key, KeyEvent e) {
    return updater.onKeyDown(key, e);
  }
  
  @Override
  public boolean onKeyUp(int key, KeyEvent e) {
    return updater.onKeyUp(key, e);
  }

  public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    // Do nothing. App is set not to allow screen resizes.
  }
  
  
  
  private class EndGameDialogHandler extends Handler {
    
    
   
    @Override
    public void handleMessage(Message msg) {
      AlertDialog dialog = new AlertDialog.Builder(context)
          .setTitle("Game Over")
          .setMessage("Final Score: " + GameState.player.experience)
          .create();
      
      dialog.setButton(
          AlertDialog.BUTTON1, "Play Again",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              game.restartGame();
            }
          });
      
      dialog.setButton(
          AlertDialog.BUTTON2, "Quit",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              GameState.activity.finish();
            }
          });
      
      dialog.show();
    }
  };
}
