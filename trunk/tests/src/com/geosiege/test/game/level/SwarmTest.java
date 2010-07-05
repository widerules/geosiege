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

package com.geosiege.test.game.level;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.geosiege.game.core.Map;
import com.geosiege.game.level.EnemyStockpile;
import com.geosiege.game.level.SpawnPattern;
import com.geosiege.game.level.Swarm;
import com.geosiege.game.level.SpawnPattern.SpawnPoint;

/**
 * Unit tests for {@link Swarm};
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class SwarmTest extends TestCase {

  private Swarm swarm;
  private Swarm priorSwarm;
  EnemyStockpile enemyStockpile;
  
  @Override
  public void setUp() {
    // Create a dummy supply of enemies
    enemyStockpile = new EnemyStockpile();
    enemyStockpile.createSupply(MockEnemyShip.class, 10);
    
    // Dummy map for the enemies to be spawned into.
    Map map = new Map();
    
    // Fake pattern with a single enemy in the middle.
    SpawnPattern pattern = new SpawnPattern();
    pattern.spawnPoints = new ArrayList<SpawnPoint>();
    pattern.spawnPoints.add(new SpawnPoint(.5f, .5f, MockEnemyShip.class));
    
    priorSwarm = new Swarm(enemyStockpile, map);
    swarm = new Swarm(enemyStockpile, map);
    swarm.priorSwarm = priorSwarm;
    swarm.pattern = pattern;
  }
  
  /**
   * Tests that a swarm is triggered automatically when the prior one is dead.
   */
  public void testUpdate_withNoPriorSwarm() {
    assertFalse(swarm.triggered);
    
    swarm.priorSwarm = null;
    swarm.update(0);
    
    assertTrue(swarm.triggered);
  }
  
  /**
   * Tests that a swarm is not triggered when the prior swarm is still alive.
   */
  public void testUpdate_withUntriggeredPriorSwarm() {
    priorSwarm.triggered = false;
    
    swarm.triggerType = Swarm.TRIGGER_AFTER_DELAY;
    swarm.triggerDelay = 0;
    
    swarm.update(0);
    
    assertFalse(swarm.triggered);
  }
  
  /**
   * Tests that a swarm is triggered immediately if it has no delay set.
   */
  public void testUpdate_withDelayTrigger_noDelay() {
    priorSwarm.triggered = true;

    swarm.triggerType = Swarm.TRIGGER_AFTER_DELAY;
    swarm.triggerDelay = 0;
    
    assertFalse(swarm.triggered);
    
    swarm.update(0);
    
    assertTrue(swarm.triggered);
  }
  
  /**
   * Tests that swarm spawn delays are working.
   */
  public void testUpdate_withDelayTrigger_withDelay() {
    priorSwarm.triggered = true;
    
    swarm.triggerType = Swarm.TRIGGER_AFTER_DELAY;
    swarm.triggerDelay = 100;
    
    assertFalse(swarm.triggered);
    
    swarm.update(0);
    assertFalse(swarm.triggered);
    
    swarm.update(50);
    assertFalse(swarm.triggered);
    
    swarm.update(60);
    assertTrue(swarm.triggered);
  }
  
  /**
   * Tests that the swarm trigger of spawning only when all the prior swarms
   * elements are dead is working.
   */
  public void testUpdate_withAllDeadTrigger() {
    MockEnemyShip enemy = enemyStockpile.take(MockEnemyShip.class);
    enemy.active = true;
    
    priorSwarm.triggered = true;
    priorSwarm.spawnedShips.add(enemy);
    
    swarm.priorSwarm = priorSwarm;
    swarm.triggerType = Swarm.TRIGGER_AFTER_LAST_DEAD;
    swarm.triggerDelay = 0;
    
    // Start with the prior swarm still alive.
    swarm.update(0);
    assertFalse(swarm.triggered);
    
    // Kill the prior swarm and assert that the swarm is triggered now.
    enemy.kill();
    swarm.update(0);
    assertTrue(swarm.triggered);
  }
  
  /**
   * Tests that the swarm trigger of spawning only when the prior swarms
   * reaches a certain death ratio is working.
   */
  public void testUpdate_withDeadRatioTrigger() {
    MockEnemyShip enemy1 = enemyStockpile.take(MockEnemyShip.class);
    MockEnemyShip enemy2 = enemyStockpile.take(MockEnemyShip.class);
    MockEnemyShip enemy3 = enemyStockpile.take(MockEnemyShip.class);
    enemy1.active = true;
    enemy2.active = true;
    enemy3.active = true;
    
    priorSwarm.triggered = true;
    priorSwarm.spawnedShips.add(enemy1);
    priorSwarm.spawnedShips.add(enemy2);
    priorSwarm.spawnedShips.add(enemy3);
    
    swarm.priorSwarm = priorSwarm;
    swarm.triggerType = Swarm.TRIGGER_AFTER_LAST_DEAD_RATIO;
    swarm.triggerDeadRatio = .5f;
    
    // Test whent they are all still alive
    swarm.update(0);
    assertFalse(swarm.triggered);
    
    // Kill one
    enemy1.kill();
    swarm.update(0);
    assertFalse(swarm.triggered);
    
    // Kill the second one, this should put the dead over the ratio.
    enemy2.kill();
    swarm.update(0);
    assertTrue(swarm.triggered);
  }
}
