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

import java.util.ArrayList;

import com.geosiege.game.guns.control.GunControl;
import com.geosiege.game.ships.Ship;

public class GunBuilder { 
  
  public static int TYPE_GROUP = 0;
  public static int TYPE_BULLETS = 1;
  
  private static final float DEFAULT_OFFSET = 40f;
  private static final float DEFAULT_BULLET_SPEED = 120f;
  private static final int DEFAULT_MAX_BULLETS = 50;
  private static final int DEFAULT_TYPE = TYPE_BULLETS;
  private static final int DEFAULT_COOLDOWN = 100;
  
  private int type = DEFAULT_TYPE;
  private Ship owner;
  private float xOffset = 0;
  private float yOffset = 0;
  private int multiplier = 1;
  private int maxBullets = DEFAULT_MAX_BULLETS;
  private float fireOffset = DEFAULT_OFFSET;
  private float bulletSpeed = DEFAULT_BULLET_SPEED;
  private int cooldown = DEFAULT_COOLDOWN;
  private ArrayList<Gun> guns;
  private GunControl control;
  private boolean autoFire = false;
  
  public GunBuilder() {
    guns = new ArrayList<Gun>();
  }
  
  public GunBuilder withOwner(Ship owner) {
    this.owner = owner;
    return this;
  }
  
  public GunBuilder withType(int type) {
    this.type = type;
    return this;
  }
  
  public GunBuilder withPosition(float xOffset, float yOffset) {
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    return this;
  }
  
  public GunBuilder withSubGun(Gun gun) {
    if (this.type != TYPE_GROUP) {
      throw new IllegalArgumentException("You can only specify a subgun " +
          "when the gun is of type 'group'");
    }
    guns.add(gun);
    return this;
  }
  
  public GunBuilder withControl(GunControl control) {
    this.control = control;
    return this;
  }
  
  public GunBuilder withMaxBullets(int maxBullets) {
    this.maxBullets = maxBullets;
    return this;
  }
  
  public GunBuilder withCooldown(int cooldown) {
    this.cooldown = cooldown;
    return this;
  }
  
  public GunBuilder withFireOffset(float fireOffset) {
    this.fireOffset = fireOffset;
    return this;
  }
  
  public GunBuilder withBulletSpeed(float bulletSpeed) {
    this.bulletSpeed = bulletSpeed;
    return this;
  }
  
  public GunBuilder withMultipler(int multiplier) {
    this.multiplier = multiplier;
    return this;
  }
  
  public GunBuilder withAutoFire(boolean auto) {
    this.autoFire = auto;
    return this;
  }
  
  public Gun build() {

    Gun gun;
    if (type == TYPE_GROUP) {
      gun = new GunGroup(guns);
    } else {
      gun = new BulletGun();
    }
    
    gun.owner = owner;
    gun.xOffset = xOffset;
    gun.yOffset = yOffset;
    gun.maxBullets = maxBullets;
    gun.fireCooldown = cooldown;
    gun.fireOffset = fireOffset;
    gun.bulletSpeed = bulletSpeed;
    gun.control = control;
    gun.autoFire = autoFire;
    gun.init();
    
    if (multiplier != 1) {
      // TODO: Create Double, Triple, and Quad guns here.
    }
    
    return gun;
  }
}
