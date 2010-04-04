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

import com.geosiege.game.guns.Gun;
import com.geosiege.game.ships.Ship;

public class DirectionalGunControl extends GunControl {
  
  private static final float DEFAULT_ANGLE_OFFSET = 0;
  
  public Ship owner;
  public float angleOffset;
  
  public DirectionalGunControl(Ship owner) {
    this(owner, DEFAULT_ANGLE_OFFSET);
  }
  
  public DirectionalGunControl(Ship owner, float angleOffset) {
    this.owner = owner;
    this.angleOffset = angleOffset;
  }
  
  @Override
  public boolean shouldFire(Gun gun) {
    return true;
  }
  
  @Override
  public void aim(Gun gun) {
    gun.aimAngle = owner.angle + angleOffset;
  }
}
