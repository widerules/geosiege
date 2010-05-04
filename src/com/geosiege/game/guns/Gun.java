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

import android.graphics.Canvas;

import com.geosiege.common.Component;
import com.geosiege.game.guns.control.GunControl;
import com.geosiege.game.ships.Ship;

public class Gun extends Component {
  
  protected Ship owner;
  protected int fireCooldown;
  protected int maxBullets;
  protected float fireOffset;
  protected float bulletSpeed;
  protected GunControl control;
  protected boolean autoFire;
  protected float aimAngle = 0;
  
  protected float xOffset;
  protected float yOffset;
  protected long lastFire;

  public Gun() {
    
  }
  
  public void init() {
    
  }
  
  protected void aimGun() {
    if (control != null) {
      control.aim(this);
    }
  }
  
  public boolean canFire() {
    long now = System.currentTimeMillis();
    return  now - lastFire > fireCooldown;
  }
  
  public boolean shouldFire() {
    return control != null ? control.shouldFire(this) : true;
  }
  
  public void recordFire() {
    lastFire = System.currentTimeMillis();
  }
  
  public void fire() {
    
  }
  
  public void draw(Canvas canvas) {
    
  }
  
  public void reset() {
    
  }

  public void update(long time) {
    if (autoFire) {
      fire();
    }
  }
  
  public void setMaxBullets(int maxBullets) {
    this.maxBullets = maxBullets;
  }
  
  public void setFireOffset(float fireOffset) {
    this.fireOffset = fireOffset;
  }
  
  public void setBulletSpeed(float bulletSpeed) {
    this.bulletSpeed = bulletSpeed;
  }
  
  public void setFireCooldown(int fireCooldown) {
    this.fireCooldown = fireCooldown;
  }
  
  public void setGunControl(GunControl control) {
    this.control = control;
  }
  
  public void setAutoFire(boolean autoFire) {
    this.autoFire = autoFire;
  }
  
  public void setAimAngle(float aimAngle) {
    this.aimAngle = aimAngle;
  }
 
  public float getAimAngle() {
    return aimAngle;
  }
}
