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

import com.geosiege.game.level.EnemyStockpile;

/**
 * Tests for {@link EnemyStockpile}.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class EnemyStockpileTest extends TestCase{
  
  public void testCreateSupply() {
    EnemyStockpile enemyStockpile = new EnemyStockpile();
    enemyStockpile.createSupply(MockEnemyShip.class, 10);
    
    assertNotNull(enemyStockpile.getSupply(MockEnemyShip.class));
  }
  
  public void testTake() {
    EnemyStockpile enemyStockpile = new EnemyStockpile();
    enemyStockpile.createSupply(MockEnemyShip.class, 10);
    
    assertNotNull(enemyStockpile.take(MockEnemyShip.class));
  }
}
