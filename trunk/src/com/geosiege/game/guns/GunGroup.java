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

import android.graphics.Canvas;

import com.geosiege.game.guns.control.GroupGunControl;

public class GunGroup extends Gun {

  ArrayList<Gun> guns;
  
  public GunGroup(ArrayList<Gun> guns) {
    this.guns = guns;
    for (Gun gun : guns) {
      if (gun.control == null) {
        gun.control = new GroupGunControl(this, gun.aimAngle);
      }
    }
  }
  
  public void fire() {
    
    aimGun();
    
    Gun gun;
    int size = guns.size();
    for ( int i = 0 ; i < size ; i++) {
      gun = guns.get(i);
      gun.fire();
    }
  }
  
  public void draw(Canvas canvas) {
    Gun gun;
    int size = guns.size();
    for ( int i = 0 ; i < size ; i++) {
      gun = guns.get(i);
      gun.draw(canvas);
    }
  }

  public void update(long time) {
    super.update(time);
    
    Gun gun;
    int size = guns.size();
    for ( int i = 0 ; i < size ; i++) {
      gun = guns.get(i);
      gun.update(time);
    }
  }
  
  public void reset() {
    Gun gun;
    int size = guns.size();
    for ( int i = 0 ; i < size ; i++) {
      gun = guns.get(i);
      gun.reset();
    }
  }

  public void setMaxBullets(int maxBullets) {
    for (Gun gun : guns)
      gun.maxBullets = maxBullets;
  }
  
  public void setFireOffset(float fireOffset) {
    for (Gun gun : guns)
      gun.fireOffset = fireOffset;
  }
  
  public void setBulletSpeed(float bulletSpeed) {
    for (Gun gun : guns)
      gun.bulletSpeed = bulletSpeed;
  }
  
  public void setFireCooldown(int fireCooldown) {
    for (Gun gun : guns)
      gun.fireCooldown = fireCooldown;
  }
}
