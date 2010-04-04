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

package com.geosiege.game.guns.control;

import java.util.Random;

import com.geosiege.game.guns.Gun;
import com.geosiege.game.ships.Ship;

public class AimingGunControl extends GunControl {

  Ship owner;
  Ship target;
  float jitter;
  float minDistance;
  Random random;

  public AimingGunControl(Ship owner, Ship target, float minDistance) {
    this(owner, target, minDistance, 0);
  }

  public AimingGunControl(Ship owner, Ship target, float minDistance, float jitter) {
    this.owner = owner;
    this.target = target;
    this.minDistance = minDistance;
    this.jitter = jitter;
    random = new Random();
  }

  public boolean shouldFire(Gun gun) {
    return true;
  }
  
  public void aim(Gun gun) {
    
    float jitterX = -jitter / 2 + random.nextFloat() * jitter;
    float jitterY = -jitter / 2 + random.nextFloat() * jitter;
    
    float angle = (float) Math.toDegrees(
        Math.atan( (target.y + jitterY - owner.y) / (target.x + jitterX - owner.x)));
    if (target.x + jitterX < owner.x)
      angle += 180;
    gun.aimAngle = angle;
  }
}
