package com.geosiege.game.level;

import com.geosiege.game.core.Map;
import com.zeddic.game.common.util.ObjectStockpile;

/**
 * Builds a new {@link Swarm} of enemies.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class SwarmBuilder {
  
  Swarm swarm;
  
  public SwarmBuilder(ObjectStockpile stockpile, Map map) {
    swarm = new Swarm(stockpile, map);
  }
  
  public SwarmBuilder afterSwarm(Swarm priorSwarm) {
    swarm.priorSwarm = priorSwarm;
    return this;
  }
  
  public SwarmBuilder withPattern(SpawnPattern pattern) {
    swarm.pattern = pattern;
    return this;
  }
  
  public SwarmBuilder withPattern(String patternString) {
    SpawnPattern pattern = new SpawnPattern();
    pattern.parsePattern(patternString);
    return withPattern(pattern);
  }
  
  public SwarmBuilder withSpawnDelay(int delay) {
    swarm.triggerDelay = delay;
    return this;
  }
  
  public SwarmBuilder withSpawnTime(int time) {
    swarm.spawnTime = time;
    return this;
  }
  
  public SwarmBuilder spawnedAfterDelay() {
    swarm.triggerType = Swarm.TRIGGER_AFTER_DELAY;
    return this;
  }
  
  public SwarmBuilder spawnedAfterPreviousSwarmDead() {
    swarm.triggerType = Swarm.TRIGGER_AFTER_LAST_DEAD;
    return this;
  }
  
  public SwarmBuilder spawnedAfterPreviousSwarmDeathRatio() {
    swarm.triggerType = Swarm.TRIGGER_AFTER_LAST_DEAD_RATIO;
    return this;
  }
  
  
  public SwarmBuilder withPreviousSwarmDeathRatio(float ratio) {
    swarm.triggerDeadRatio = ratio;
    return this;
  }
  
  public SwarmBuilder withSpawnScale(int scale) {
    swarm.scale = scale;
    return this;
  }
  
  public Swarm build() {
    return swarm;
  }
}
