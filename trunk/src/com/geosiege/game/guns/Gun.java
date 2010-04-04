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

  public Ship owner;
  public float xOffset;
  public float yOffset;
  public int maxBullets;
  public int fireCooldown;
  public long lastFire;
  public float fireOffset;
  public float bulletSpeed;
  public GunControl control;
  public float aimAngle = 0;
  public boolean autoFire;
  
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
}
