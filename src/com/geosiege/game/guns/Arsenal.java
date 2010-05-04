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

package com.geosiege.game.guns;

import com.geosiege.game.guns.control.DirectionalGunControl;
import com.geosiege.game.ships.Ship;

public class Arsenal {
  
  public static Gun getPeaShooter(Ship owner) {
    Gun gun = new GunBuilder()
      .withOwner(owner)
      .withType(GunBuilder.TYPE_BULLETS)
      .withBulletSpeed(80f)
      .withMaxBullets(50)
      .withCooldown(100)
      .withFireOffset(40)
      .build();
    return gun;
  }
  
  
  public static Gun getDeathBlossom(Ship owner) {
    Gun gun1 = getPeaShooter(owner);
    gun1.control = new DirectionalGunControl(owner, 0);
    gun1.fireOffset = 20;
    //gun1.autoFire = true;
    
    Gun gun2 = getPeaShooter(owner);
    gun2.control = new DirectionalGunControl(owner, 180);
    gun2.fireOffset = 20;
    //gun2.autoFire = true;
    
    Gun combo = new GunBuilder()
      .withType(GunBuilder.TYPE_GROUP)
      .withSubGun(gun1)
      .withSubGun(gun2)
      .build();
    
    return combo;
  }
}