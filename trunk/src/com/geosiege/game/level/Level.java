package com.geosiege.game.level;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

import com.geosiege.game.core.Map;
import com.geosiege.game.effects.CountdownEffect;
import com.geosiege.game.effects.TextFlasher;
import com.geosiege.game.highscore.HighScores;
import com.zeddic.game.common.GameObject;

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
  
  /** A unique id identifying the level. */
  public int id;
  
  /** True if the player has completed the level. */
  public boolean complete;
  
  /** True if the player is fighting the last swarm. */
  public boolean onLastSwarm;
  
  /** The players personal high score for this level. */
  public int highscore;
  
  /** Whether the player has set a high score yet. */
  public boolean highscoreSet;
  
  /** The map with obstacles. */
  public Map map;
  
  /** The the order in which swarms should be spawned. */
  public List<Swarm> swarms = new ArrayList<Swarm>();

  /** The index of the next swarm to spawn. */
  private int nextSwarmIndex = 0;
  
  /** The next swarm that will be spawned. */
  private Swarm nextSwarm;
  
  /** Countdown animation before the game starts. */
  private CountdownEffect countdown;

  private long firstSpawnDelay = 0;
  
  public Level() {
    this(0);
  }
  
  public Level(long firstSpawnDelay) {
    this.map = new Map();
    this.firstSpawnDelay = firstSpawnDelay;
    this.complete = false;
  }
  
  public void loadScores(HighScores scores) {
    highscoreSet = scores.containsScore(id);
    highscore = scores.getHighScore(id);
  }
  
  public void start() {
    this.countdown = new CountdownEffect(4, new TextFlasher());
    this.countdown.start();
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
      nextSwarm.triggerDelay += firstSpawnDelay;
    }
  }
  
  @Override
  public void draw(Canvas c) {
    map.draw(c);
  }
  
  @Override
  public void update(long time) {
    
    map.update(time);
    countdown.update(time);
    
    if (!countdown.isDone()) {
      return;
    }
    
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
