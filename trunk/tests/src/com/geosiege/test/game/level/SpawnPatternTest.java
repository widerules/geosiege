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

import com.geosiege.game.level.SpawnPattern;
import com.geosiege.game.level.SpawnPattern.SpawnPoint;
import com.geosiege.game.ships.DeathStar;
import com.geosiege.game.ships.EnemyShip;
import com.geosiege.game.ships.SimpleEnemyShip;

/**
 * Tests for {@link SpawnPattern}.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class SpawnPatternTest extends TestCase {
  
  SpawnPattern pattern = new SpawnPattern();
  
  private static float ROUND_PRECISION = 100;
  
  public void testParsePattern_withSimplePattern() {
    pattern.parsePattern(
        "xx\n" +
        "xx"
    );
    
    assertSpawnPointExists(0, 0, SimpleEnemyShip.class);
    assertSpawnPointExists(0, 1, SimpleEnemyShip.class);
    assertSpawnPointExists(1, 1, SimpleEnemyShip.class);
    assertSpawnPointExists(1, 0, SimpleEnemyShip.class);
  }
  
  public void testParsePattern_withCenterEnemies() {
    pattern.parsePattern(
        " x \n" +
        "x x\n" +
        " x "
    );
    
    assertSpawnPointExists(.5f, 0f, SimpleEnemyShip.class);
    assertSpawnPointExists(0f, .5f, SimpleEnemyShip.class);
    assertSpawnPointExists(1f, .5f, SimpleEnemyShip.class);
    assertSpawnPointExists(.5f, 1f, SimpleEnemyShip.class);
  }
  
  public void testParsePattern_withMultipleEnemyTypes() {
    pattern.parsePattern(
        "*  \n" +
        " x \n" +
        "  *"
    );
    
    assertSpawnPointExists(0f, 0f, DeathStar.class);
    assertSpawnPointExists(.5f, .5f, SimpleEnemyShip.class);
    assertSpawnPointExists(1f, 1f, DeathStar.class);
  }
  
  public void testParsePattern_withPlayer() {
    pattern.parsePattern(
        "x  \n" +
        " p \n" +
        "  x"
    );
    
    assertSpawnPointExists(-.5f, -.5f, SimpleEnemyShip.class);
    assertSpawnPointExists(.5f, .5f, SimpleEnemyShip.class);
  }
  
  public void testParsePattern_withTooSmallMatrix() {
    try {
      pattern.parsePattern("x");
    } catch (IllegalArgumentException expected) {
      return;
    }
    fail("Parse should have failed.");
  }
  
  public void testParsePattern_withUnequalRols() {
    try {
      pattern.parsePattern("x\n  x");
    } catch (IllegalArgumentException expected) {
      return;
    }
    fail("Parse should have failed.");
  }
  
  private void assertSpawnPointExists(
      float x,
      float y,
      Class<? extends EnemyShip> shipType) {
    
    for (SpawnPoint point : pattern.spawnPoints) {
      if (round(point.xPercent) == round(x) &&
          round(point.yPercent) == round(y) &&
          point.shipType == shipType) {
        return;
      }
    }
    
    fail(String.format("Could not find %s at %s, %s in pattern.",
        shipType.toString(), x, y));
  }
  
  private float round(float value) {
    return Math.round(value * ROUND_PRECISION) / ROUND_PRECISION;
  }
}
