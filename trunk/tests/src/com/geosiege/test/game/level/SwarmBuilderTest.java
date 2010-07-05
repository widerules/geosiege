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

import junit.framework.TestCase;

import com.geosiege.game.core.Map;
import com.geosiege.game.level.EnemyStockpile;
import com.geosiege.game.level.SpawnPattern;
import com.geosiege.game.level.Swarm;
import com.geosiege.game.level.SwarmBuilder;

/**
 * Tests the {@link SwarmBuilder}.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class SwarmBuilderTest extends TestCase {

  private static final String PATTERN = 
      "  \n" +
      " x";
  
  EnemyStockpile enemyStockpile;
  Map map;
  Swarm priorSwarm;
  
  @Override
  public void setUp() {
    enemyStockpile = new EnemyStockpile();
    map = new Map();
    priorSwarm = new Swarm(enemyStockpile, map);
  }
  
  public void testBuild_withSpawnedAfterDelay() {
    Swarm swarm = new SwarmBuilder(enemyStockpile, map)
        .afterSwarm(priorSwarm)
        .withPattern(PATTERN)
        .spawnedAfterDelay()
        .withSpawnDelay(10)
        .withSpawnTime(200)
        .build();
    
    assertEquals(Swarm.TRIGGER_AFTER_DELAY, swarm.triggerType);
    assertEquals(10, swarm.triggerDelay);
    assertEquals(200, swarm.spawnTime);
  }
  
  public void testBuild_withSpawnedAfterLastDead() {
    SpawnPattern pattern = new SpawnPattern();
    Swarm swarm = new SwarmBuilder(enemyStockpile, map)
        .afterSwarm(priorSwarm)
        .withPattern(pattern)
        .spawnedAfterPreviousSwarmDead()
        .withSpawnDelay(10)
        .build();
    
    assertEquals(Swarm.TRIGGER_AFTER_LAST_DEAD, swarm.triggerType);
    assertEquals(10, swarm.triggerDelay);
    assertEquals(pattern, swarm.pattern);
  }
}
