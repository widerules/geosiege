package com.geosiege.game.level;

import java.util.ArrayList;
import java.util.List;

import com.geosiege.common.GameObject;
import com.geosiege.game.core.GameState;
import com.geosiege.game.core.Map;
import com.geosiege.game.level.SpawnPattern.SpawnPoint;
import com.geosiege.game.ships.EnemyShip;

/**
 * Spawns a group of enemies into the world in a pattern. The spawn time of 
 * the enemies is triggered by the state of the swarm before it, allowing
 * multiple swarms to be chained into a level.
 * 
 * Triggers include: 
 * - Spawn after a given delay
 * - Spawn after the previous swarm has died
 * - Spawn after the previous swarm has reached a given death ratio. IE:
 *   90% of the previous swarm is dead.
 *   
 * Note that a swarm can ONLY spawn after the swarm before it has been 
 * 'triggered'. A swarm is 'triggered' after it's trigger condition has been
 * met and any delay transpired. If a spawn has no prior spawn specified, it
 * will meet any trigger conditions by default.
 * 
 * Depending on the pattern, swarms can be set to either spawn in a pattern
 * spread out around the world, or in a pattern closely surrounding the player.
 * 
 * For simplicity, it is recommended to use the SwarmBuilder when building a 
 * new swarm.
 * 
 * @author baileys (Scott Bailey)
 */
public class Swarm extends GameObject {
  
  /** How long, by default, swarms should take to spawn. */
  public static final int DEFAULT_SPAWN_TIME = 2000;
  
  /** A trigger where the swarm will spawn after a given delay. */
  public static final int TRIGGER_AFTER_DELAY = 0;
  
  /** A trigger where the swarm will spawn after the prior swarm is all dead. */
  public static final int TRIGGER_AFTER_LAST_DEAD = 1;
  
  /** A trigger to spawn when the prior swarm is X% dead. */
  public static final int TRIGGER_AFTER_LAST_DEAD_RATIO = 2;
  
  /** A pattern to spawn the swarm in. */
  public SpawnPattern pattern;
  
  /** The type of trigger to use. */
  public int triggerType = TRIGGER_AFTER_DELAY;
  
  /** The time delay to wait after a swarm has met a trigger condition. */
  public int triggerDelay = 0;
  
  /** 
   * If using the DEAD_RATIO trigger, this is the target ratio of the prior
   * swarm. For example: .9 if you will spawn when the prior swarm reaches 90%
   * dead.
   */
  public float triggerDeadRatio = 1;
  
  /**
   * How long it should take for the swarm to spawn.
   */
  public int spawnTime = DEFAULT_SPAWN_TIME;
  
  /**
   * A scale in world units of how wide the spawn should be. This ONLY applies
   * when the swarm is spawning around a player, as specified in the spawn
   * pattern.
   */
  public int scale = 0;
  
  /** True if the swarm has been triggered and started spawning. */
  public boolean triggered = false;
  
  /** The prior swarm before this one, used when evaluating triggers. */
  public Swarm priorSwarm = null;
  
  /** A list of all ships spawned by this swarm. */
  public List<EnemyShip> spawnedShips = new ArrayList<EnemyShip>();

  /** If a countdown has been started after a trigger condition has been met. */
  private boolean countingDown = false;
  
  /** 
   * A countdown of any time delay to wait between a trigger condition being 
   * met and actual spawning to occur. When this reaches 0, enemies can be
   * spawned.
   */
  private long countdownTime;
  
  /** Stockpile where enemies can be obtained. */
  private final EnemyStockpile enemyStockpile;
  
  /** The world map. */
  private final Map map;
  
  /** A cached flag for when all the ships die. */
  private boolean allDead = false;
  
  public Swarm(EnemyStockpile enemyStockpile, Map map) {
    this.enemyStockpile = enemyStockpile;
    this.map = map;
  }
  
  @Override
  public void update(long time) {
    // If already triggered, nothing left to do.
    if (triggered) {
      return;
    }
    
    if (countingDown) {
      checkIfCoutdownComplete(time);
      return;
    }
    
    // If there is no prior swarm, immediately start a countdown to spawn
    // this one.
    if (priorSwarm == null) {
      startCountdown(time);
      return;
    }
    
    // Wait until the last swarm has been triggered before even considering
    // triggering this one.
    if (!priorSwarm.triggered) {
      return;
    }
    
    // Check out the particular trigger types.
    
    if (triggerType == TRIGGER_AFTER_DELAY) {
      startCountdown(time);
      
    } else if (triggerType == TRIGGER_AFTER_LAST_DEAD && priorSwarm.getAllDead()) {
      startCountdown(time);
      
    } else if (triggerType == TRIGGER_AFTER_LAST_DEAD_RATIO &&
        priorSwarm.getDeadRatio() >= triggerDeadRatio) {
      
      startCountdown(time);
    }
  }
  
  /**
   * Returns the percentage of this swarm that is dead. Returns 0 if no
   * ships have been spawned yet.
   */
  public float getDeadRatio() {
    if (allDead) {
      return 1;
    }
    
    if (spawnedShips == null) {
      return 0;
    }
    
    float deadCount = 0;
    for (EnemyShip ship : spawnedShips) {
      if (ship.isDead()) {
        deadCount++;
      }
    }
    float ratio = deadCount / (float) spawnedShips.size();
    if (ratio == 1) {
      // cache the all dead calculation in case any of the spawned
      // ships end up being recycled.
      allDead = true;
    }
    return ratio;
  }
  
  /**
   * Returns true if all ships spawned by this swarm are dead.
   */
  public boolean getAllDead() {
    return getDeadRatio() == 1;
  }
  
  private void startCountdown(long time) {
    countingDown = true;
    countdownTime = triggerDelay;
    checkIfCoutdownComplete(time);
  }
  
  private void checkIfCoutdownComplete(long time) {
    countdownTime -= time;
    if (countdownTime <= 0) {
      trigger();
    }
  }
  
  /**
   * Triggers the swarm by spawning them into the world.
   */
  private void trigger() {
    triggered = true;
    
    for (SpawnPoint spawnPoint : pattern.spawnPoints) {
      EnemyShip ship = enemyStockpile.take(spawnPoint.shipType);

      if (ship == null) {
        continue;
      }
      
      float x = 0;
      float y = 0;
      
      if (pattern.centeredOnPlayer) {
        x = GameState.playerShip.x + scale * spawnPoint.xPercent;
        y = GameState.playerShip.y + scale * spawnPoint.yPercent;
      } else {
        x = map.spawnLeft + map.spawnWidth * spawnPoint.xPercent;
        y = map.spawnTop + map.spawnHeight * spawnPoint.yPercent;
      }

      if (map.inSpawnableArea(x, y)) {
        spawnedShips.add(ship);
        ship.spawn(x, y, spawnTime);      
      }
    } 
  }
}
