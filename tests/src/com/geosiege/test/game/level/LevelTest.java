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

import java.io.IOException;

import android.test.InstrumentationTestCase;

import com.geosiege.common.util.ResourceLoader;
import com.geosiege.game.level.EnemyStockpile;
import com.geosiege.game.level.Level;
import com.geosiege.game.level.LevelLoader;
import com.geosiege.game.level.Swarm;
import com.geosiege.game.ships.EnemyShip;

/**
 * Tests for {@Level}.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class LevelTest extends InstrumentationTestCase {

  private static final String TEST_LEVEL = "levels/level_test.txt";
  
  Level level;
  LevelLoader loader;
  EnemyStockpile stockpile;
  
  @Override
  public void setUp() {
    // Resource loader must be prepared for the loader to get files.
    ResourceLoader.init(getInstrumentation().getContext());
    
    stockpile = new MockEnemyStockpile();  
    loader = new LevelLoader(stockpile);   
  }
  
  public void testUpdate() throws IOException {
    level = loader.loadLevel(TEST_LEVEL);
    
    // Start off triggering no one.
    level.update(0);
    assertFalse(level.swarms.get(0).triggered);
    assertFalse(level.complete);
    
    // Trigger the first swarm.
    level.update(50);
    assertTrue(level.swarms.get(0).triggered);
    assertFalse(level.swarms.get(1).triggered);
    assertFalse(level.complete);
    
    // Pass some time, but don't trigger the second swarm.
    level.update(30);
    assertTrue(level.swarms.get(0).triggered);
    assertFalse(level.swarms.get(1).triggered);
    
    // Finally trigger the second swarm.
    level.update(20);
    assertTrue(level.swarms.get(0).triggered);
    assertTrue(level.swarms.get(1).triggered);
    assertFalse(level.swarms.get(2).triggered);
    assertFalse(level.onLastSwarm);
    
    // Pass some time - make sure the last swarm is not triggered since the
    // previous swarm is not yet dead.
    level.update(50);
    assertFalse(level.swarms.get(2).triggered);
    assertFalse(level.onLastSwarm);
    assertFalse(level.complete);
    
    // Kill swarm 2, then update again. 
    killSwarm(level.swarms.get(1));
    level.update(50);
    assertTrue(level.swarms.get(2).triggered);
    assertTrue(level.onLastSwarm);
    assertFalse(level.complete);
    
    // Kill just the last swarm, verify that the game doesn't think its over.
    killSwarm(level.swarms.get(2));
    level.update(0);
    assertFalse(level.complete);
    
    // Kill the last remaining swarm - verify that the level now knows its done.
    killSwarm(level.swarms.get(0));
    level.update(0);
    assertTrue(level.complete);    
  }
  
  private void killSwarm(Swarm swarm) {
    for (EnemyShip ship : swarm.spawnedShips) {
      ship.kill();
    }
  }
  
  /**
   * A de-fanged stockpile that just returns mock ships regardless of what is
   * requested.
   */
  private class MockEnemyStockpile extends EnemyStockpile {
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends EnemyShip> T take(Class<T> shipType) {
      return (T) new MockEnemyShip();
    }
  }
}
