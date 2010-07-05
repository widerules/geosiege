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
import android.graphics.Path;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

import com.geosiege.common.GameObject;

public class Map extends GameObject {
  
  public static Map map = null;
  
  private static final float EDGE_BUFFER = 100;
  private static final float DEFAULT_WIDTH = 400;
  private static final float DEFAULT_HEIGHT = 400;
  ScrollingBackgroundLayer background;
  
  public float width;
  public float height;
  public float top;
  public float left;
  public float right;
  public float bottom;

  Path borderPath; 
  Paint paint;
  
  public Map() {
    paint = new Paint();
    paint.setStyle(Style.STROKE);
    paint.setStrokeWidth(3);
    paint.setStrokeJoin(Join.MITER);
    paint.setColor(Color.WHITE);

    map = this;
    
    background = new ScrollingBackgroundLayer(this);
    
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }
  
  public void setSize(float width, float height) {
    this.width = width;
    this.height = height;
    top = 0 + EDGE_BUFFER;
    left = 0 + EDGE_BUFFER;
    right = left + width; 
    bottom = top + height;
    
    borderPath = new Path();
    borderPath.moveTo(left, top);
    borderPath.lineTo(right, top);
    borderPath.lineTo(right, bottom);
    borderPath.lineTo(left, bottom);
    borderPath.lineTo(left, top);
  }
  
  public void popuplate() {
    
  }
  
  public void update(long time) {
    background.update(time);
  }
  
  public void draw(Canvas c) {
    background.draw(c);
    c.drawPath(borderPath, paint);
  }
}

/*
 * 
 * 
  
  List<Obstacle> obstacles;
  List<EnemyShip> ships;
  EnemySpawner<SimpleEnemyShip> spawner;
  EnemySpawner<DeathStar> deathSpawner;
  
 */

/*
 * obstacles = new ArrayList<Obstacle>();
    Obstacle temp;
    Random rand = new Random();
    for ( int i = 0 ; i < 0 ; i++) {
      temp = new Obstacle(EDGE_BUFFER + rand.nextFloat() * (width-500), EDGE_BUFFER + rand.nextFloat() * (height - 500));
      temp.setAngle(rand.nextFloat() * 360);
      temp.setVelocity(-5 + rand.nextFloat() * 10, -5 + rand.nextFloat() * 10);
      temp.setScale(2f + rand.nextFloat() * 5);
      temp.rotation = -.5f + rand.nextFloat() * .5f;
      obstacles.add(temp);
    }
    
    spawner.init();
    deathSpawner.init();
*/

/*
 * Obstacle obstacle; 
    Iterator<Obstacle> iterator = obstacles.iterator();
    while (iterator.hasNext()) {
      obstacle = iterator.next();
      if (obstacle.active) {
        obstacle.update(time);
      }
    }
    
    spawner.update(time);
    deathSpawner.update(time); 
*/

/*
 * c.drawPath(borderPath, paint);
    
    for (GameObject obstacle : obstacles) {
      if (obstacle.active) {
        obstacle.draw(c);
      }
    }
    
    spawner.draw(c);
    deathSpawner.draw(c);
    
*/
