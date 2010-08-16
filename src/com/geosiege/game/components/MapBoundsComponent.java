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

package com.geosiege.game.components;

import com.geosiege.common.Component;
import com.geosiege.common.PhysicalObject;
import com.geosiege.common.util.Vector2d;
import com.geosiege.game.core.GameState;
import com.geosiege.game.core.Map;

/**
 * A component that determines have an object should behave once it
 * gets to the edge of the map.
 * 
 * @author scott@zeddic.com (Scott Bailey)
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
    Map map = GameState.level.map;
    hitHorizontal = false;
    hitVertical = false;
   
    // Calculate how much the 
    float radius = parent.bounds.shape.radius;
    
    // Calculate how much the ship bounds have 'overstepped' the map edges.
    // If any of these values are > 0, it means the object is out of bounds.
    float topOverstep = (map.top) - (parent.y - radius);
    float bottomOverstep = (parent.y + radius) - (map.bottom);
    float leftOverstep = (map.left) - (parent.x - radius);
    float rightOverstep = (parent.x + radius) - (map.right);
    
    // If it is out of bounds on any count, trigger a collision to get it
    // back in bounds.
    if (topOverstep > 0 || bottomOverstep > 0 || leftOverstep > 0 || rightOverstep > 0) {
      
      translation.y = 0;
      if (topOverstep > 0) 
        translation.y = topOverstep;
      else if (bottomOverstep > 0)
        translation.y = -bottomOverstep;
      
      translation.x = 0;
      if (leftOverstep > 0)
        translation.x = leftOverstep;
      else if(rightOverstep >0)
        translation.x = -rightOverstep;
      
      parent.collide(null, translation);
    }
    
    // Hit horizontal and bouncing.
    if (behavior == BEHAVIOR_BOUNCE && (leftOverstep > 0 || rightOverstep > 0))
      parent.velocity.x *= -1;
    
    // Hit vertical and bouncing.
    if (behavior == BEHAVIOR_BOUNCE && (topOverstep > 0 || bottomOverstep >0))
      parent.velocity.y *= -1;
  }
}
