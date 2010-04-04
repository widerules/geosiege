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

package com.geosiege.common.collision;

import android.util.Log;

import com.geosiege.common.PhysicalObject;

/**
 * Represents a single position within the {@link CollisionGrid}. Contains
 * a list of PhysicalObjects that are currently residing within that position.
 * 
 * @author baileys (Scott Bailey)
 */
public class GridSpot {
  
  private static int INITIAL_SPOT_CAPACITY = 10;
  private static int CAPACITY_GROWTH_RATE = 2;
  
  /**
   * The objects in this spot. Note: purposefully using a primative array
   * here for performance reasons, even though this means certain calculations
   * need to be done manually.
   */
  public PhysicalObject[] objects;
  
  /**
   * The number of objects in this spot.
   */
  public int numObjects;
  
  public GridSpot() {
    objects = new PhysicalObject[INITIAL_SPOT_CAPACITY];
  }
  
  /**
   * Adds a new object to this grid position.
   */
  public void add(PhysicalObject object) {
    
    if (contains(object)) 
      return;
    
    if (numObjects == objects.length) {
      grow();
    }
    
    objects[numObjects] = object;
    numObjects++;
  }
  
  public boolean contains(PhysicalObject object) {
    for (int i = 0 ; i < numObjects ; i++) {
      if (objects[i] == object) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Removes an object from the grid spot.
   */
  public void remove(PhysicalObject object) {
    for (int i = 0 ; i < numObjects ; i++) {
      if (objects[i] == object) {
        for ( int j = i ; j < numObjects - 1 ; j++) {
          objects[j] = objects[j + 1];
        }
        numObjects--;
        break;
      }
    } 
  }
  
  /**
   * Grows the spots internal array for holding objects.
   */
  private void grow() {
    
    int newSize = objects.length * CAPACITY_GROWTH_RATE;
    
    // Due to the costs of garbage collection in android, the initial capacity
    // should be large enough 
    Log.d(GridSpot.class.getCanonicalName(), "Growing GridSpot from size " + 
        objects.length + " to " + newSize + ". Initial grid capacity should " + 
        "be increased to avoid this.");
    
    PhysicalObject[] newArray = new PhysicalObject[newSize];
    for ( int i = 0 ; i < objects.length ; i++) {
      newArray[i] = objects[i];
    }
    objects = newArray;
  }
}
