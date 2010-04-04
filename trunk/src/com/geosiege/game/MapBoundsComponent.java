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

import com.geosiege.common.Component;
import com.geosiege.common.PhysicalObject;
import com.geosiege.common.util.Vector2d;
import com.geosiege.game.core.GameState;
import com.geosiege.game.core.Map;

/**
 * A component that determines have an object should behave once it
 * gets to the edge of the map.
 * 
 * @author baileys
 */
public class MapBoundsComponent extends Component {
  
  public static final int BEHAVIOR_BOUNCE = 1;
  public static final int BEHAVIOR_HIT_WALL = 2;
  public static final int BEHAVIOR_COLLIDE = 3;
  
  public PhysicalObject parent;
  public boolean hitHorizontal;
  public boolean hitVertical;
  public int behavior;
  public MapBoundsComponent(PhysicalObject parent, int behavior) {
    this.parent = parent;
    this.behavior = behavior;
  }
  
  
  Vector2d translation = new Vector2d(0, 0);
  public void update(long time) {
    Map map = GameState.map;
    hitHorizontal = false;
    hitVertical = false;
    if (parent.x + parent.bounds.shape.radius > map.right ||
        parent.x - parent.bounds.shape.radius < map.left) {
      hitHorizontal = true;
    }
    if (parent.y + parent.bounds.shape.radius > map.bottom ||
        parent.y - parent.bounds.shape.radius < map.top) {
      hitVertical = true;
    }
    
    if (hitHorizontal || hitVertical) {
      float timeFraction = time / PhysicalObject.TIME_SCALER;
      translation.x = hitHorizontal ? -parent.velocity.x * timeFraction : 0;
      translation.y = hitVertical ? -parent.velocity.y * timeFraction : 0;
      parent.collide(null, translation);
    }
    
    if (behavior == BEHAVIOR_BOUNCE && hitHorizontal)
      parent.velocity.x *= -1;
    
    if (behavior == BEHAVIOR_BOUNCE && hitVertical)
      parent.velocity.y *= -1;
  }
}
