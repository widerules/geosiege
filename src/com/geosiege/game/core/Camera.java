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

/**
 * A camera that centers the view around the user. The camera uses a 
 * 'window frame' approach. If the player's ship remains within this window,
 * the camera doesn't move. If the player moves outside the window, the
 * camera adjusts itself by the bare minimum amount so that the player
 * remains in the window.
 * 
 * The camera will stop moving once it reaches a certain upper bound to
 * the world edge.
 */
public class Camera {
  
  /**
   * The vertical edges of the screen that make up the window edge.
   * If a user goes into this part of the screen the camera will pan up.
   * Value is expressed as a percentage of the screen size that will be
   * taken up by one window edge. Value should not exceed .5.
   */
  private static final float VERTICAL_WINDOW_BUFFER = .4f;
  
  /**
   * The horizontal edges of the screen that make up the window edge.
   */
  private static final float HORIZONTAL_WINDOW_BUFFER = .35f;

  /**
   * How far into the edge of the world the camera will allow to be visible. 
   * Negative values allow the world ends to be seen while positive values
   * hide the world edge. A value of 0 has the camera stop panning at
   * exactly the edge of the world.
   */
  private static final float MAP_EDGE_BUFFER = -250;
 
  private float x = Float.MAX_VALUE;
  private float y = Float.MAX_VALUE;
  public float width;
  public float height;
  private float screenHalfWidth;
  private float screenHalfHeight;
  private boolean change; 
  private float verticalWindowEdgeBuffer;
  private float horizontalWindowEdgeBuffer;
  
  /**
   * The top of the visible camera area in world coordinates.
   */
  public float top;
  
  /**
   * The bottom of the visible camera area in world coordinates.
   */
  public float bottom;
  
  /**
   * The left of the visible camera area in world coordinates.
   */
  public float left;
  
  /**
   * The right of the visible camera area in world coordinates.
   */
  public float right;

  /**
   * The singleton camera instance.
   */
  public static Camera camera;
  
  public Camera(float x, float y) {
    updateScreenSize(GameState.screenWidth, GameState.screenHeight);
    ensureOnScreen(x, y);
    camera = this;
  }
  
  public synchronized void updateScreenSize(float screenWidth, float screenHeight) {
    this.screenHalfWidth = screenWidth / 2;
    this.screenHalfHeight = screenHeight / 2;
    this.width = screenWidth;
    this.height = screenHeight;
    
    verticalWindowEdgeBuffer = screenHeight * VERTICAL_WINDOW_BUFFER;
    horizontalWindowEdgeBuffer = screenWidth * HORIZONTAL_WINDOW_BUFFER;
    buildBounds();
  }
  
  public synchronized void apply(Canvas c) {
    c.save();
    float transX = screenHalfWidth - x;
    float transY = screenHalfHeight - y;
    c.translate(transX, transY);
  }
  
  public void buildBounds() {
    left = x - screenHalfWidth;
    right = x + screenHalfWidth;
    top = y - screenHalfHeight;
    bottom = y + screenHalfHeight;
  }
  
  public void revert(Canvas c) {
    c.restore();
  }
  
  public synchronized void ensureOnScreen(float cameraX, float cameraY) {
    Map map = GameState.map;
    change = false;

    
    // Ensures that the given camera x and y coordinates are visible 
    // on the screen. The camera works as a window into the world. The
    // following ensures that the player is visible within that window. It
    // is only when the user gets to the edge of the window that it starts
    // to follow or move. If the player moves around the center of the window, 
    // there is no need to move the camera. 
    
    // Too far to right of window?
    if (cameraX > x + screenHalfWidth - horizontalWindowEdgeBuffer) {
      x = Math.min(
              cameraX + horizontalWindowEdgeBuffer,
              map.right - MAP_EDGE_BUFFER)
            - screenHalfWidth; 
      change = true;
    }
    // Too far to left of window?
    else if (cameraX < x - screenHalfWidth + horizontalWindowEdgeBuffer) {
      x = Math.max(
              cameraX - horizontalWindowEdgeBuffer,
              map.left + MAP_EDGE_BUFFER)
            + screenHalfWidth;
      change = true;
    }
    
    // Too far to top of window?
    if (cameraY > y + screenHalfHeight - verticalWindowEdgeBuffer) {
      y = Math.min(
              cameraY + verticalWindowEdgeBuffer,
              map.bottom - MAP_EDGE_BUFFER)
            - screenHalfHeight;
      change = true;
    }
    // Too far to bottom of window?
    else if (cameraY < y - screenHalfHeight + verticalWindowEdgeBuffer) {
      y = Math.max(
              cameraY - verticalWindowEdgeBuffer,
              map.top + MAP_EDGE_BUFFER)
            + screenHalfHeight;
      change = true;
    }
    
    if (change) {
      buildBounds();
    }
  }
}
