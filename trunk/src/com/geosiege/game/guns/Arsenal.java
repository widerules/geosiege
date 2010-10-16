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
import com.geosiege.game.guns.control.RandomGunControl;
import com.geosiege.game.ships.Ship;

public class Arsenal {
  
  public static Gun getPeaShooter(Ship owner) {
    Gun gun = new GunBuilder()
      .withOwner(owner)
      .withBulletSpeed(60f)
      .withCooldown(300)
      .withFireOffset(40)
      .build();

    return gun;
  }
  
  public static Gun getSniper(Ship owner) {
    Gun gun = new GunBuilder()
      .withOwner(owner)
      .withBulletSpeed(120f)
      .withCooldown(2500)
      .withFireOffset(40)
      .withBullet(ShardBullet.class)
      .build();
    
    return gun;
  }
  
  public static Gun getTriGun(Ship owner) {
    Gun gun = getPeaShooter(owner);
    gun.multiplier = 3;
    gun.multiplierStartAngle = -5;
    gun.multiplierAngleBetweenBullets = 5;
    
    return gun;
  }
  
  public static Gun getBombBurst(Ship owner) {
    Gun gun = new GunBuilder()
      .withOwner(owner)
      .withBulletSpeed(110f)
      .withCooldown(0)
      .withFireOffset(20)
      .withClipSize(10)
      .withReloadTime(2000)
      .withAutoFire(false)
      .withControl(new RandomGunControl())
      .withBullet(ShardBullet.class)
      .build();
    
    return gun;
  }
  
  public static Gun getDeathBlossom(Ship owner) {
    Gun gun = getPeaShooter(owner);
    gun.multiplier = 2;
    gun.multiplierAngleBetweenBullets = 180;
    gun.setAutoFire(true);
    gun.setFireOffset(20);
    gun.setGunControl(new DirectionalGunControl(owner));
    return gun;
  }
}
