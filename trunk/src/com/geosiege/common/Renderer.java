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

package com.geosiege.common;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.SurfaceHolder;

import com.geosiege.game.core.Game;

public class Renderer {
  
  private static final int MAX_SHUTDOWN_WAIT_TIME = 2000;
  private static final boolean REPORT_FPS = true;
  
  private final SurfaceHolder surfaceHolder; 
  private final Thread thread; 
  private final Game game;
  private boolean run;
  private Paint paint;
  
  private int frameCount = 0;
  private long timestamp = 0;
  private int fps = 0;
  private RenderTask task;
  
  public Renderer(Game game, SurfaceHolder surfaceHolder) {
    this.game = game;
    this.surfaceHolder = surfaceHolder;
    this.task = new RenderTask();
    this.thread = new Thread(task);
    this.paint = new Paint();
    this.paint.setColor(Color.WHITE);
  }
  
  public void start() {
    run = true;
    if (thread.isAlive()) {
      thread.resume();
      return;
    }
    thread.start();
  }
  
  public void stop() {
    run = false;
    
    boolean retry = true;
    while (retry) {
      try {
        thread.join(MAX_SHUTDOWN_WAIT_TIME);
        retry = false;
      } catch (InterruptedException e) {}
    }
    
    if (thread.isAlive()) {
      thread.stop();
    }
  }
  
  public void pause() {
    thread.suspend();
  }

  /**
   * Draws the ship, fuel/speed bars, and background to the provided
   * Canvas.
   */
  private void draw(Canvas canvas) {
    canvas.drawColor(Color.BLACK, Mode.SRC_IN);
    
    //game.draw(canvas);
    
    if (REPORT_FPS) {
      frameCount++;
      long now = System.currentTimeMillis();
      if ( now - timestamp > 1000) {
         fps = frameCount / (int)((now - timestamp) / 1000);
         timestamp = now;
         frameCount = 0;
      }
      canvas.save();
      canvas.scale(2, 2);
      canvas.drawText(Integer.toString(fps), 20, 20, paint);
      canvas.restore();
    }

    canvas.restore();
  }
  
  private class RenderTask implements Runnable {

    public void run() {

      while (run) {
        Canvas c = null;
        try {
          c = surfaceHolder.lockCanvas(null);
          synchronized (surfaceHolder) {
            draw(c);
          }
        } catch (Exception e) {
          Log.i(Renderer.class.getName(), "Error rendering: " + e.toString());
        } finally {
          // do this in a finally so that if an exception is thrown
          // during the above, we don't leave the Surface in an
          // inconsistent state
          if (c != null) {
            surfaceHolder.unlockCanvasAndPost(c);
          }
        }
      }
    }
  }
}
