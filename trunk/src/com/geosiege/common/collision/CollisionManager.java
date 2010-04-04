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

import android.util.FloatMath;

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.util.Circle;
import com.geosiege.common.util.Polygon;
import com.geosiege.common.util.Span;
import com.geosiege.common.util.Vector2d;

/**
 * Performs collision detection for objects in the world. 
 * 
 * The bulk of this class is determining 'narrow' based collision detection.
 * That is, checking whether two very specific objects currently touch
 * or overlap. This detection is done using the Seperating Axis Therom.
 * 
 * The CollisionManager makes use of a {@link CollisionGrid} to do 'broad'
 * based collision detection. When checking whether an object has collided
 * the manager asks the grid of nearby objects, which it can then do 'narrow'
 * checks against. This increases processing speed by reducing the detailed
 * collision calculations that need to be done.
 * 
 * @author baileys (Scott Bailey)
 */
public class CollisionManager {
  
  // The types of collision detection that can be performed for objects.
  // These are set when adding a CollisionComponent to an object.
  
  /**
   * An object that can hit other objects but can never be hit. 
   * This is useful when many of a given type of object may exist
   * in the world, but it doesn't matter if anything runs into them. 
   * For example, bullets, where you want to avoid wasted checks to see
   * if bullets have hit other bullets.
   */
  public static final int TYPE_HIT_ONLY = 1;
  
  /**
   * An object that can hit other objects and be hit. For example:
   * the players ship.
   */
  public static final int TYPE_HIT_RECEIVE = 2;
  
  /**
   * An object that can receive collisions but can never hit something.
   * For example: asteroids that can't be hit by each other but can be hit
   * by a player ship. 
   */
  public static final int TYPE_RECEIVE_ONLY = 3;
  
  /**
   * A large object that is stationary and can be run into by other
   * objects in the world.
   */
  public static final int TYPE_STATIONARY = 4;

  /**  The one and only collision manager. */
  public static CollisionManager singleton;
  
  /** The world size to use for a grid. */
  private static final int GRID_SIZE = 50;
  
  /** The grid for broad base collision detect. */
  private CollisionGrid grid; 
  
  /** Array to receive nearby objects when doing a collision query. */
  GridSpot[] nearby = new GridSpot[5];
  
  /** Any game specific collision check optimizations. */
  private CustomCollisionCheck customCollisionCheck;
  
  /**
   * Creates a new collision manager for a world of the specific size.
   */
  private CollisionManager(float width, float height) {
    grid = new CollisionGrid(width + GRID_SIZE, height + GRID_SIZE, GRID_SIZE);
  }
  
  /**
   * Adds an object that should be tracked by the collision system.
   */
  public void addObject(CollisionComponent object) {
    grid.addObject(object);
  }
  
  /**
   * Adds a stationary obstacle to be tracked by the collion system.
   */
  public void addStationaryObject(CollisionComponent object) {
    grid.addStationaryObject(object);
  }
  
  /**
   * Updates a objects position within the collision system.
   */
  public void updatePosition(CollisionComponent src) {
    grid.updatePosition(src);
  }
  
  /**
   * Allows a game to specify any additional optimizations that should be done
   * when performing collision checking. For example, certain types of objects
   * may not need to collide in certain types of games.
   */
  public void setCustomCollisionCheck(CustomCollisionCheck collisionCheck) {
    this.customCollisionCheck = collisionCheck;
  }
  
  /**
   * Check for any collision between this object and any others nearby.
   * Objects that collide will be notified by calling their collide method.
   */
  public void checkForCollision(CollisionComponent src, long time) {
    // Ask the grid for a set of lists that represent objects in 
    // in nearby grid positions. Some lists may be null if there are 
    // no objects in that grid position currently. 
    grid.getNearbyGridSpots(src, nearby);
    GridSpot spot; 
    PhysicalObject object;
    for ( int i = 0 ; i < nearby.length ; i++) {
      spot = nearby[i];
      if ( spot != null) {
        for ( int j = 0 ; j < spot.numObjects ; j++) {
          object = spot.objects[j];
          if (src.object == object || object.active == false) {
            continue;
          }
          checkForCollision(src.object, object, time);
        }
      }
    }
  }
  

  // Vectors and objects used in the checkForCollision method. 
  // They are created once and reused to prevent them from being instantiated
  // and causing GC to be run.
  Vector2d axis = new Vector2d(0, 0);  
  Span spanA = new Span(0, 0);
  Span spanB = new Span(0, 0);
  Vector2d scaledVelocityVector = new Vector2d(0, 0);
  Vector2d translationVector = new Vector2d(0, 0);
  Vector2d objectsSeperationVector = new Vector2d(0, 0);
  Vector2d srcTranslation = new Vector2d(0, 0);
  Vector2d destTranslation = new Vector2d(0, 0);
  
  /**
   * Check for a collision between the two objects using the separating axis
   * theorem. 
   */
  private void checkForCollision(PhysicalObject src, PhysicalObject dest, long time) {

    // Check for any custom collision checking.
    if (customCollisionCheck != null &&
        customCollisionCheck.shouldSkipCollisionCheck(src, dest, time)) {
      return;
    }
    
    // Shortcut: If two circles are colliding, rely on the simple Circle
    // collision check to handle everything, including notifying objects
    // of collisions.
    if (src.bounds.shape instanceof Circle && dest.bounds.shape instanceof Circle) {
      simpleCircleCollision(src, dest, time, true);
      return;
    }
    
    // Check for the possibility of a collision by comparing two object's
    // general radius. This can quickly rule out cases where collisions
    // are not possible before trying the full, heavy, seperating axis
    // theorem.
    if (!simpleCircleCollision(src, dest, time, false)) {
      return;
    }

    float minDistanceBetween = Float.MAX_VALUE;
    
    // Assume that the polygons intersect until proved otherwise.
    boolean intersect = true;
    boolean willIntersect = true;
    float velocityProjection = 0;
    
    int numSrcAxis = getNumAxis(src, dest);
    int numDestAxis = getNumAxis(dest, src);
    int totalAxis = numSrcAxis + numDestAxis;

    // Iterate through each edge and see if there is space between them.
    // This is an implementation of the separating axis theorem. 
    for (int edgeIndex = 0; edgeIndex < totalAxis; edgeIndex++) {
      // Determine the edge to test.
      if (edgeIndex < numSrcAxis) {
        getAxis(src, dest, edgeIndex, axis);
      } else {
        getAxis(dest, src, edgeIndex - numSrcAxis, axis);
      }
      
      // Project both polygons onto the axis
      src.bounds.shape.projectOnAxis(src.x, src.y, axis, spanA);
      dest.bounds.shape.projectOnAxis(dest.x, dest.y, axis, spanB);
      
      // If there is any space between the projected shadows, you know they
      // aren't touching.
      float distanceBetween = spanA.distanceBetween(spanB);
      if (distanceBetween > 0) {
        intersect = false;
      }
      
      // Now to determine if the objects _will_ collide.
      
      // Project the velocity on the current axis
      scaledVelocityVector.x = src.velocity.x * time / 200;
      scaledVelocityVector.y = src.velocity.y * time / 200;
      velocityProjection = axis.dotProduct(scaledVelocityVector);
      if (velocityProjection < 0) {
          spanA.min += velocityProjection;
      } else {
          spanA.max += velocityProjection;
      }

      // See if they intersect now.
      distanceBetween = spanA.distanceBetween(spanB);
      if (distanceBetween > 0) 
        willIntersect = false;

      // If the polygons are not intersecting and won't intersect, exit the loop
      if (!intersect && !willIntersect)
        break;

      // Next keep track of the axis that represented the smallest distance
      // from not colliding. This is used to create a translation Vector, which
      // represents the smallest possible distance the src object needs to move
      // to no longer be colliding. 

      distanceBetween = Math.abs(distanceBetween);
  
      if (distanceBetween < minDistanceBetween) {
        
        // New minimum distance
        minDistanceBetween = distanceBetween;
        translationVector.x = axis.x;
        translationVector.y = axis.y;
        
        objectsSeperationVector.x = src.x - dest.x;
        objectsSeperationVector.y = src.y - dest.y;
        if (objectsSeperationVector.dotProduct(translationVector) < 0) {
          translationVector.x *= -1;
          translationVector.y *= -1;
        }
        
      }     
    }
    
    if (intersect || willIntersect) {
      splitTranslationVector(src, dest, translationVector, minDistanceBetween);
    }
  }
  
  /**
   * Gets the combined number of axis that should be tested for the 
   * src polygon, given that it will be tested against the dest polygon.
   * Knowing the dest polygon that will be tested against is needed for cases
   * where the collision might test between a circle and a polygon, in which
   * the axis don't represent a 1-to-1 relationship with polygon edges.
   */
  public int getNumAxis(PhysicalObject src, PhysicalObject dest) {
    
    // When polygons are tested against polygons, edges are used
    // to determine test axis
    if (src.bounds.shape instanceof Polygon) {
      return ((Polygon) src.bounds.shape).edges.length;
    } else if (src.bounds.shape instanceof Circle) {
      
      // If testing between two circles, you only have 1 axis to test against:
      // the axis separating their centers.
      if (dest.bounds.shape instanceof Circle) {
        return 1;
      } 
      
      // If testing between a polygon and a circle, you must test
      // all axis between the polygon points and the circle center.
      else if (dest.bounds.shape instanceof Polygon) {
        return ((Polygon) dest.bounds.shape).points.length;
      }
    }
    return 0;
  }
  
  /**
   * Retrieves a specified axis of the src polygon, given that it will be
   * tested against the dest polygon for a collision. Returns the axis
   * specified by i and places the vector values into the axis parameter.
   */
  public void getAxis(PhysicalObject src, PhysicalObject dest, int i, Vector2d axis) {
    if (src.bounds.shape instanceof Polygon) {
      Vector2d edge = ((Polygon) src.bounds.shape).edges[i];
      axis.x = -edge.y;
      axis.y = edge.x;
    } else if (src.bounds.shape instanceof Circle) {
      if (dest.bounds.shape instanceof Circle) {
        axis.x = dest.x - src.x;
        axis.y = dest.y - src.y;
      } else if (dest.bounds.shape instanceof Polygon) {
        Vector2d point = ((Polygon) dest.bounds.shape).points[i];
        axis.x = point.x - src.x;
        axis.y = point.y - src.y;
      }
    } else {
      axis.x = 1;
      axis.y = 0;
    }
    axis.normalize();
  }
  
  /**
   * Performs a simple version of collision checking by checking if
   * the bounding circle of two objects intersect. This check is very quick
   * compared to the separating axis theorem. Returns true if a collision is
   * possible, false otherwise. If requested, this method can also be told to
   * notify the src and dest objects if they collide. This should be set to
   * true if this collision detection is the only collision detection
   * used between two objects.
   */
  private boolean simpleCircleCollision(
      PhysicalObject src,
      PhysicalObject dest,
      long time,
      boolean notifyObjectsOnCollide) {
    
    // Are they colliding now?
    float dX = src.x - dest.x;
    float dY = src.y - dest.y;
    float minDistance = src.bounds.shape.radius + dest.bounds.shape.radius;
    boolean collide = dX * dX + dY * dY < minDistance * minDistance;
    
    // Will they collide?
    dX = (src.x + src.velocity.x * time / PhysicalObject.TIME_SCALER) - dest.x; 
    dY = (src.y + src.velocity.y * time / PhysicalObject.TIME_SCALER) - dest.y;
    boolean willCollide = dX * dX + dY * dY < minDistance * minDistance;
    
    
    if (notifyObjectsOnCollide && (collide || willCollide)) {
      
      // Find the distance needed to make it so these two circles are not
      // touching. This distance will be turned into a vector and split
      // between the two objects, so they are both pushed back.
      float seperationNeeded = minDistance - FloatMath.sqrt(dX * dX + dY * dY);
      translationVector.x = dX;
      translationVector.y = dY;
      if (translationVector.x == 0 && translationVector.y == 0)
        translationVector.x = 1;
      translationVector.normalize();
      
      splitTranslationVector(src, dest, translationVector, seperationNeeded);
    }
    
    return collide || willCollide;
  }
  
  /**
   * Takes a translation vector (a vector that when applied to either of the 
   * objects would make them no longer collide), and splits it between the 
   * two objects based on their relative sizes.
   */
  private void splitTranslationVector(
      PhysicalObject src,
      PhysicalObject dest,
      Vector2d translation,
      float seperationNeeded) {
    
    float areaA = ((float) Math.PI) * src.bounds.shape.radius * src.bounds.shape.radius;
    float areaB = ((float) Math.PI) * dest.bounds.shape.radius * dest.bounds.shape.radius;
    float totalArea = areaA + areaB; 
    float aDistance = areaA / totalArea * seperationNeeded; 
    float bDistance = areaB / totalArea * seperationNeeded;
    
    srcTranslation.x = translation.x * bDistance;
    srcTranslation.y = translation.y * bDistance;
    
    destTranslation.x = translation.x * aDistance * -1;
    destTranslation.y = translation.y * aDistance * -1;
    
    src.collide(dest, srcTranslation);
    dest.collide(src, destTranslation);
    
  }
  
  public static CollisionManager setup(float width, float height) {
    singleton = new CollisionManager(width, height);
    return singleton;
  }
  
  public static CollisionManager get() { 
    return singleton;
  }
}
