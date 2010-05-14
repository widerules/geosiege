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

package com.geosiege.game.ships;

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.explosion.ExplosionManager;
import com.geosiege.common.util.Vector2d;
import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.Bullet;


public class EnemyShip extends Ship {
   
  public boolean killed;
  public int exp = 1;

  public EnemyShip(float x, float y) {
    super(x, y);
    killed = false;
  }
  
  public void reset() {
    active = true;
    killed = false;
  }
  
  @Override
  public void collide(PhysicalObject object, Vector2d avoidVector) {
    super.collide(object, avoidVector);
    
    if (object instanceof Bullet) {
      if (!killed)
        die();
    }
  }
  
  public void die() {
    this.killed = true;
    ExplosionManager.get().explode(x, y);
    ExplosionManager.get().explodeWithGravity(x, y, GameState.playerShip);
    GameState.player.addExp(exp);
  }
}
