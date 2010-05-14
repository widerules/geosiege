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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

import com.geosiege.common.GameObject;
import com.geosiege.game.Obstacle;
import com.geosiege.game.ships.DeathStar;
import com.geosiege.game.ships.EnemyShip;
import com.geosiege.game.ships.SimpleEnemyShip;

public class Map extends GameObject {
  
  public static Map map = null;
  
  private static final float EDGE_BUFFER = 100;
  ScrollingBackgroundLayer background;
  
  
  public float width = 1000;
  public float height = 1000;
  public float top;
  public float left;
  public float right;
  public float bottom;
  
  List<Obstacle> obstacles;
  List<EnemyShip> ships;
  Path borderPath; 
  Paint paint;
  
  EnemySpawner spawner;
  EnemySpawner deathSpawner;

  
  public Map() {
    
    top = 0 + EDGE_BUFFER;
    left = 0 + EDGE_BUFFER;
    right = left + width; 
    bottom = top + height;
    
   //  CollisionManager.setup(width, height);
   
    
    borderPath = new Path();
    borderPath.moveTo(left, top);
    borderPath.lineTo(right, top);
    borderPath.lineTo(right, bottom);
    borderPath.lineTo(left, bottom);
    borderPath.lineTo(left, top);
    
    paint = new Paint();
    paint.setStyle(Style.STROKE);
    paint.setStrokeWidth(3);
    paint.setStrokeJoin(Join.MITER);
    paint.setColor(Color.WHITE);

    map = this;
    
    background = new ScrollingBackgroundLayer(this);
    
    spawner = new EnemySpawner(SimpleEnemyShip.class, 15, 5000);
    deathSpawner = new EnemySpawner(DeathStar.class, 2, 7000);
    
    spawner.setSpawn(600, 600, 400, 400);
    deathSpawner.setSpawn(600, 600, 400, 400);
  }
  
  public void popuplate() {
    obstacles = new ArrayList<Obstacle>();
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
  }
  
  public void update(long time) {
    background.update(time);
    
    Obstacle obstacle; 
    Iterator<Obstacle> iterator = obstacles.iterator();
    while (iterator.hasNext()) {
      obstacle = iterator.next();
      if (obstacle.active) {
        obstacle.update(time);
      }
    }
    
    spawner.update(time);
    deathSpawner.update(time);
  }
  
  public void draw(Canvas c) {
    background.draw(c);
    c.drawPath(borderPath, paint);
    
    for (GameObject obstacle : obstacles) {
      if (obstacle.active) {
        obstacle.draw(c);
      }
    }
    
    spawner.draw(c);
    deathSpawner.draw(c);
  }
}
