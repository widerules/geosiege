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

import com.geosiege.game.core.GameState;
import com.geosiege.game.guns.control.GunControl;
import com.geosiege.game.ships.EnemyShip;
import com.zeddic.game.common.Component;
import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.util.Countdown;

public class Gun extends Component {
  
  protected PhysicalObject owner;
  protected int fireCooldown;
  protected float fireOffset;
  protected float bulletSpeed;
  protected float bulletDamage;
  protected GunControl control;
  protected boolean autoFire;
  protected float aimAngle = 0;
  protected int clipSize;
  protected long reloadTime;
  protected Class<? extends Bullet> bulletClass;

  protected int multiplier = 1;
  protected float multiplierStartAngle = 0;
  protected float multiplierAngleBetweenBullets = 0;
  
  protected float xOffset;
  protected float yOffset;
  protected long lastFire;

  private int clipCount;
  private Countdown reloadTimer;
  private boolean reloading;
  
  public Gun() {
    
  }
  
  public void init() {
    reloadTimer = new Countdown(reloadTime);
    reset();
  }
  
  protected void aimGun() {
    if (control != null) {
      control.aim(this);
    }
  }
  
  public boolean canFire() {
    return !reloading;
  }
  
  public boolean shouldFire() {
    return control != null ? control.shouldFire(this) : true;
  }
  
  /*public void recordFire() {
    lastFire = System.currentTimeMillis();
    clipCount--;
    
  } */
  
  public boolean fire() {

    if (!canFire() || !shouldFire())
      return false;
    
    boolean shot = false;
    
    long now = System.currentTimeMillis();
    long passedTime = now - lastFire;
    if (passedTime > fireCooldown) {
      long timesToFire = (fireCooldown == 0 ? clipCount : passedTime / fireCooldown);
      for (int i = 0 ; i < timesToFire ; i++) {
        
        fireOnce();
        shot = true;
        
        clipCount--;
        
        if (clipCount <= 0) {
          reloadTimer.reset();
          reloadTimer.start();
          reloading = true;
        }
      }
      lastFire = (fireCooldown == 0 ? now : now - passedTime % fireCooldown);
    }
    
    return shot;
  }
  
  private void fireOnce() {

    aimGun();
    
    float fireAngle = multiplierStartAngle + aimAngle;
    
    for (int i = 0 ; i < multiplier ; i++) {
      
      Bullet bullet = GameState.stockpiles.bullets.take(bulletClass);
      if (bullet == null) {
        return;
      }
      bullet.x = owner.x + xOffset;
      bullet.y = owner.y + yOffset;
      bullet.setAngle(fireAngle);
      bullet.setVelocityBySpeed(fireAngle, bulletSpeed);
      bullet.offset(fireOffset);
      bullet.firedByEnemy = (owner instanceof EnemyShip);
      bullet.enable();
      bullet.life = 0;
      
      fireAngle += multiplierAngleBetweenBullets;
      
    }
  }
  
  public void draw(Canvas canvas) {
    
  }
  
  public void reset() {
    lastFire = System.currentTimeMillis();
    reloading = false;
    clipCount = clipSize;
  }

  public void update(long time) {
    if (reloading) {
      reloadTimer.update(time);
      if (reloadTimer.isDone()) {
        reloading = false;
        clipCount = clipSize;
      }
    }
    
    if (autoFire) {
      fire();
    }
  }
  
  public void setFireOffset(float fireOffset) {
    this.fireOffset = fireOffset;
  }
  
  public void setBulletSpeed(float bulletSpeed) {
    this.bulletSpeed = bulletSpeed;
  }
  
  public void setBulletDamage(float bulletDamage) {
    this.bulletDamage = bulletDamage;
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
 
  public void setClipSize(int clipSize) {
    this.clipSize = clipSize;
    this.clipCount = clipSize;
  }
  
  public void setReloadTime(long reloadTime) {
    this.reloadTime = reloadTime;
    this.reloadTimer.duration = reloadTime;
  }
  
  public float getAimAngle() {
    return aimAngle;
  }
}
