package com.geosiege.game.level;

import java.util.ArrayList;
import java.util.List;

import com.geosiege.common.GameObject;
import com.geosiege.game.core.Map;

/**
 * A single level or round within the game. A level is made up of:
 * 
 * - A map with some set of bounds
 * - A set of swarms that are spawned in a given order.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class Level extends GameObject {
  
  /** 
   * The maximum number of swarms to check to see if they are all dead
   * when determining if the player has completed the level.
   */
  private static final int ALL_SWARMS_DEAD_VISION_SIZE = 5;
  
  /** The name of the level. */
  public String name;
  
  /** True if the player has completed the level. */
  public boolean complete;
  
  /** True if the player is fighting the last swarm. */
  public boolean onLastSwarm;
  public Map map;
  public List<Swarm> swarms = new ArrayList<Swarm>();
  
  private int nextSwarmIndex = 0;
  private Swarm nextSwarm;
  
  public Level() {
    map = new Map();
    complete = false;
  }
  
  protected void setMapSize(float width, float height) {
    map.setSize(width, height);
  }
  
  protected void addSwarm(Swarm swarm) {
    swarms.add(swarm);
    
    if (swarms.size() > 1) {
      swarm.priorSwarm = swarms.get(swarms.size()-2);
    } else if (swarms.size() == 1) {
      nextSwarm = swarm;
    }
  }
  
  @Override
  public void update(long time) {
    // If the levels over with do nothing.
    if (complete) {
      return;
    }
    
    // If we are on the last swarm, only mark it complete after everyone
    // is dead.
    if (onLastSwarm) {
      complete = lastFewSwarmsAreDead();
      return;
    }
    
    nextSwarm.update(time);
    if (nextSwarm.triggered) {

      nextSwarmIndex++;
      if (nextSwarmIndex == swarms.size()) {
        onLastSwarm = true;
        return;
      }
      
      nextSwarm = swarms.get(nextSwarmIndex);
    }
  }
  
  /**
   * Returns true if the last couple of swarms, defined by 
   * ALL_SWARMS_DEAD_VISION_SIZE are dead. The number of swarms to check is
   * limited for performance reasons - since you could have levels with
   * a large number of swarms which could recycle the same enemies. 
   */
  private boolean lastFewSwarmsAreDead() {
    int backLimit = swarms.size() - ALL_SWARMS_DEAD_VISION_SIZE;
    for (int i = swarms.size() - 1; i >= 0 && i >= backLimit; i--) {
      if (!swarms.get(i).getAllDead()) {
        return false;
      }
    }
    
    return true;
  }
}
