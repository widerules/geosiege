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

/**
 * Unit tests for {@link LevelLoader}.
 *
 * @author scott@zeddic.com (Scott Bailey)
 */
public class LevelLoaderTest extends InstrumentationTestCase {
  
  private static final String SIMPLE_LEVEL_NAME = "SimpleName";
  private static final int SPAWN_TIME = 100;
  private static final int SPAWN_DELAY = 50;
  private static final int NO_SPAWN_DELAY = 0;
  private static final float DEAD_RATIO = .5f;
  private static final int SPAWN_SCALE = 100;
  
  EnemyStockpile enemyStockpile;
  LevelLoader loader;
  
  @Override
  public void setUp() {
    // Resource loader must be prepared for the loader to get files.
    ResourceLoader.init(getInstrumentation().getContext());
    
    enemyStockpile = new EnemyStockpile();
    loader = new LevelLoader(enemyStockpile);
  }
  
  public void testLoadLevel_simple() throws IOException {
    Level level = loader.loadLevel("levels/simple.txt");
    assertEquals(SIMPLE_LEVEL_NAME, level.name);
    assertEquals(1, level.swarms.size());
    
    Swarm swarm = level.swarms.get(0);
    assertEquals(1, swarm.pattern.spawnPoints.size());
    assertEquals(1f, swarm.pattern.spawnPoints.get(0).xPercent);
    assertEquals(1f, swarm.pattern.spawnPoints.get(0).yPercent);
    assertEquals(Swarm.TRIGGER_AFTER_DELAY, swarm.triggerType);
    assertEquals(SPAWN_DELAY, swarm.triggerDelay);
  }
  
  public void testLoadLevel_multiSwarms() throws IOException {
    Level level = loader.loadLevel("levels/multi.txt");
    assertEquals(SIMPLE_LEVEL_NAME, level.name);
    assertEquals(4, level.swarms.size());
    
    Swarm swarm;
    
    // Check swarm 1
    swarm = level.swarms.get(0);
    assertEquals(1, swarm.pattern.spawnPoints.size());
    assertEquals(1f, swarm.pattern.spawnPoints.get(0).xPercent);
    assertEquals(1f, swarm.pattern.spawnPoints.get(0).yPercent);
    assertEquals(Swarm.TRIGGER_AFTER_DELAY, swarm.triggerType);
    assertEquals(SPAWN_DELAY, swarm.triggerDelay);
    assertEquals(SPAWN_TIME, swarm.spawnTime);
    
    // Check swarm 2
    swarm = level.swarms.get(1);
    assertEquals(1, swarm.pattern.spawnPoints.size());
    assertEquals(.5f, swarm.pattern.spawnPoints.get(0).xPercent);
    assertEquals(.5f, swarm.pattern.spawnPoints.get(0).yPercent);
    assertEquals(Swarm.TRIGGER_AFTER_DELAY, swarm.triggerType);
    assertEquals(NO_SPAWN_DELAY, swarm.triggerDelay);
    
    // Check swarm 3
    swarm = level.swarms.get(2);
    assertEquals(2, swarm.pattern.spawnPoints.size());
    assertEquals(0f, swarm.pattern.spawnPoints.get(0).xPercent);
    assertEquals(0f, swarm.pattern.spawnPoints.get(0).yPercent);
    assertEquals(1f, swarm.pattern.spawnPoints.get(1).xPercent);
    assertEquals(0f, swarm.pattern.spawnPoints.get(1).yPercent);
    assertEquals(Swarm.TRIGGER_AFTER_LAST_DEAD, swarm.triggerType);
    assertEquals(NO_SPAWN_DELAY, swarm.triggerDelay);
    
    // Check swarm 4
    swarm = level.swarms.get(3);
    assertEquals(2, swarm.pattern.spawnPoints.size());
    assertEquals(0f, swarm.pattern.spawnPoints.get(0).xPercent);
    assertEquals(-.5f, swarm.pattern.spawnPoints.get(0).yPercent);
    assertEquals(0f, swarm.pattern.spawnPoints.get(1).xPercent);
    assertEquals(.5f, swarm.pattern.spawnPoints.get(1).yPercent);
    assertTrue(swarm.pattern.centeredOnPlayer);
    assertEquals(Swarm.TRIGGER_AFTER_LAST_DEAD_RATIO, swarm.triggerType);
    assertEquals(DEAD_RATIO, swarm.triggerDeadRatio);
    assertEquals(SPAWN_SCALE, swarm.scale);
    assertEquals(SPAWN_DELAY, swarm.triggerDelay);
  }
  
  public void testLoadLevel_noSwarms() {
    
    try {
      loader.loadLevel("levels/no_swarms.txt");
    } catch (IOException e) {
      return; // expected
    }
    fail("Level with no swarms should thrown an exception!");
  }
  
  public void testLoadLevel_badFormattedSwarm() {
    try {
      loader.loadLevel("levels/bad_formatted_swarm.txt");
    } catch (IOException e) {
      return; // expected
    }
    fail("Level with  should thrown an exception!");
  }
  
  public void testLoadLevel_unknownProperties() throws IOException {
    Level level = loader.loadLevel("levels/unknown_props.txt");
    assertEquals(SIMPLE_LEVEL_NAME, level.name);
    assertEquals(1, level.swarms.size());
  }
  
}
