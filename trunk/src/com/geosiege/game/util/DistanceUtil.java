package com.geosiege.game.util;

import com.zeddic.game.common.PhysicalObject;

public class DistanceUtil {

  /**
   * Returns true if the two objects are within a given range of each other.
   */
  public static boolean objectsWithinRange(PhysicalObject target1, PhysicalObject target2, float maxDistance) {
    float dX = target1.x - target2.x;
    float dY = target1.y - target2.y;
    return dX * dX + dY * dY <= maxDistance * maxDistance; 
  }
  
}
