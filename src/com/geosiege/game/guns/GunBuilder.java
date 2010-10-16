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

import com.geosiege.game.guns.control.GunControl;
import com.zeddic.game.common.PhysicalObject;

public class GunBuilder { 
  
  private static final float DEFAULT_OFFSET = 40f;
  private static final float DEFAULT_BULLET_SPEED = 120f;
  private static final float DEFAULT_BULLET_DAMAGE = 1;
  private static final int DEFAULT_COOLDOWN = 100;
  private static final int DEFAULT_CLIP_SIZE = 6;
  private static final int DEFAULT_RELOAD_TIME = 0;
  private static final Class<? extends Bullet> DEFAULT_BULLET = Bullet.class;
  
  private PhysicalObject owner;
  private float xOffset = 0;
  private float yOffset = 0;
  private int multiplier = 1;
  private float multiplierStartAngle = 0;
  private float multiplierAngleBetweenBullets = 0;
  private float fireOffset = DEFAULT_OFFSET;
  private float bulletSpeed = DEFAULT_BULLET_SPEED;
  private float bulletDamage = DEFAULT_BULLET_DAMAGE;
  private int cooldown = DEFAULT_COOLDOWN;
  private GunControl control;
  private boolean autoFire = false;
  private int clipSize = DEFAULT_CLIP_SIZE;
  private int reloadTime = DEFAULT_RELOAD_TIME;
  private Class<? extends Bullet> bulletClass = DEFAULT_BULLET;
  
  public GunBuilder() {
    
  }
  
  public GunBuilder withOwner(PhysicalObject owner) {
    this.owner = owner;
    return this;
  }
  
  public GunBuilder withPosition(float xOffset, float yOffset) {
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    return this;
  }
  
  public GunBuilder withControl(GunControl control) {
    this.control = control;
    return this;
  }
  
  public GunBuilder withCooldown(int cooldown) {
    this.cooldown = cooldown;
    return this;
  }
  
  public GunBuilder withClipSize(int clipSize) {
    this.clipSize = clipSize;
    return this;
  }
  
  public GunBuilder withReloadTime(int reloadTime) {
    this.reloadTime = reloadTime;
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
  
  public GunBuilder withBulletDamage(float bulletDamage) {
    this.bulletDamage = bulletDamage;
    return this;
  }
  
  public GunBuilder withMultipler(int multiplier) {
    multiplier = Math.max(1, multiplier);
    this.multiplier = multiplier;
    this.multiplierStartAngle = 0;
    this.multiplierAngleBetweenBullets = 360 / multiplier;
    return this;
  }
  
  public GunBuilder withMultiplier(int multiplier, float startAngle, float angleBetweenBullets) {
    this.multiplier = multiplier;
    this.multiplierStartAngle = startAngle;
    this.multiplierAngleBetweenBullets = angleBetweenBullets;
    return this;
  }
  
  public GunBuilder withAutoFire(boolean auto) {
    this.autoFire = auto;
    return this;
  }
  
  public GunBuilder withBullet(Class<? extends Bullet> bulletClass) {
    this.bulletClass = bulletClass;
    return this;
  }
  
  public Gun build() {

    Gun gun = new Gun();
    
    gun.owner = owner;
    gun.xOffset = xOffset;
    gun.yOffset = yOffset;
    gun.fireCooldown = cooldown;
    gun.fireOffset = fireOffset;
    gun.bulletSpeed = bulletSpeed;
    gun.bulletDamage = bulletDamage;
    gun.reloadTime = reloadTime;
    gun.clipSize = clipSize;
    gun.control = control;
    gun.autoFire = autoFire;
    gun.bulletClass = bulletClass;
    gun.multiplier = multiplier;
    gun.multiplierStartAngle = multiplierStartAngle;
    gun.multiplierAngleBetweenBullets = multiplierAngleBetweenBullets;
    gun.init();
    
    return gun;
  }
}
