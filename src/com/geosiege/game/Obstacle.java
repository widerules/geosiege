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

package com.geosiege.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.collision.CollisionComponent;
import com.geosiege.common.collision.CollisionManager;
import com.geosiege.common.util.Bounds;
import com.geosiege.common.util.Vector2d;
import com.geosiege.common.util.Polygon.PolygonBuilder;

public class Obstacle extends PhysicalObject {
  
  // private static final float ANGLE_OFFSET = -90;
  
  Path path;
  Paint paint;

  public Obstacle(float x, float y) {
    super(x, y);
    setupPath();
    
    paint = new Paint();
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);
    paint.setStrokeWidth(4);
    paint.setStrokeJoin(Join.BEVEL);
    paint.setStrokeCap(Cap.SQUARE);
    //paint.setPathEffect(PathEffect.)

    
    addComponent(new CollisionComponent(this, CollisionManager.TYPE_RECEIVE_ONLY));
    addComponent(new MapBoundsComponent(this, MapBoundsComponent.BEHAVIOR_BOUNCE));
  }

  private void setupPath() {
    setBounds(new Bounds(new PolygonBuilder()
        /*.add(0, 0)
        .add(100, 0)
        .add(100, 1)*/
        .add(-20, 15)
        .add(-5, 20)
        .add(10, 5)
        .add(14, -10)
        .add(0, -15)
        .add(-6, -10)
        .build()));

    path = bounds.raw.path;
  }
  
  public void draw(Canvas canvas) {
    canvas.save();
    canvas.translate(x, y);
    canvas.rotate(angle);
    canvas.scale(scale, scale);
    canvas.drawPath(bounds.raw.path, paint);
    canvas.restore();
    
    paint.setStyle(Style.FILL);
    paint.setColor(Color.WHITE);
  }
  
  
  public void collide(PhysicalObject object, Vector2d avoid) {
    super.collide(object, avoid);
    //this.active = false;
    //paint.setStyle(Style.FILL);
    //paint.setColor(Color.RED);

    /*paint.setStrokeWidth(4);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setStrokeJoin(Paint.Join.ROUND); */
  }
}
