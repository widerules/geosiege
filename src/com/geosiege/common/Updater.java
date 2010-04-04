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

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class Updater {

  public GameMode mode = null;
  
  private static final boolean REPORT_FPS = true;
  private static final int UPDATES_PER_SECOND = 60;
  private final SurfaceHolder surfaceHolder; 
  private final Timer timer;
  
  private UpdateTask updateTask;
  private long timestamp;

  private Paint paint;
  private int frameCount = 0;
  private int fps = 0;
  private long fpsTimestamp;
  
  public Handler handler;
  
  public Updater(SurfaceHolder surfaceHolder) {
    this.timer = new Timer();
    this.timestamp = System.currentTimeMillis();
    this.surfaceHolder = surfaceHolder;
    
    this.paint = new Paint();
    this.paint.setColor(Color.WHITE);
  }
  
  public void start() {
    
    long updateInterval = 1000 / UPDATES_PER_SECOND;
    updateTask = new UpdateTask();
    timer.scheduleAtFixedRate(updateTask, 0, updateInterval);
  }
  
  public void stop() {
    updateTask.cancel();
  }
  
  public void pause() {
    updateTask.cancel();
  }
  
  private void update() {
    long now = System.currentTimeMillis();
    long delta = now - timestamp;
    if (mode != null) {
      mode.update(delta);
    }
    timestamp = now;
  }
  
  private void draw() {
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
  

  /**
   * Draws the ship, fuel/speed bars, and background to the provided
   * Canvas.
   */
  private void draw(Canvas canvas) {
    canvas.drawColor(Color.BLACK, Mode.SRC_IN);
    
    if (mode != null) {
      mode.draw(canvas);
    }
    
    if (REPORT_FPS) {
      frameCount++;
      long now = System.currentTimeMillis();
      if ( now - fpsTimestamp > 1000) {
         fps = frameCount / (int)((now - fpsTimestamp) / 1000);
         fpsTimestamp = now;
         frameCount = 0;
      }
      canvas.save();
      canvas.scale(2, 2);
      canvas.drawText(Integer.toString(fps), 20, 20, paint);
      canvas.restore();
      
    }
    canvas.restore();
  }
  
  public void setMode(GameMode mode) {
    this.mode = mode;
    if (mode != null) {
      this.mode.updater = this;
    }
  }
  
  /// USER INPUT
  
  public boolean onTouchEvent(MotionEvent e) {
    return mode != null ? mode.onTouchEvent(e) : false;
  }

  public boolean onTrackballEvent(MotionEvent e) {
    return mode != null ? mode.onTrackballEvent(e) : false;
  }
  
  public boolean onKeyDown(int key, KeyEvent e) {
    return mode != null ? mode.onKeyDown(key, e) : false;
  }
  
  public boolean onKeyUp(int key, KeyEvent e) {    
    return mode != null ? mode.onKeyUp(key, e) : false;
  }
  
  private class UpdateTask extends TimerTask {
    @Override
    public void run() {
      try {
        update();
        draw();
      } catch (Exception e) {
        Log.i("zeddic", "Update Exception: " + e.toString());
      }
    }    
  }
}
