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

package com.geosiege.game.components;

import android.util.FloatMath;

import com.zeddic.game.common.Component;
import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.util.RandomUtil;

public class SimplePathComponent extends Component {

  private PhysicalObject parent;
  private PhysicalObject target;
  private float xTargetOffset;
  private float yTargetOffset;
  private float waitDistance;
  private float speed;
  
  private boolean inRange;
  private boolean enabled;
  
  
  public SimplePathComponent(
      PhysicalObject parent,
      PhysicalObject target,
      float waitDistance,
      float speed,
      float jitter) {
    
    this.parent = parent;
    this.target = target;
    this.xTargetOffset = RandomUtil.nextFloat(-jitter, jitter);
    this.yTargetOffset = RandomUtil.nextFloat(-jitter, jitter);
    this.waitDistance = waitDistance + RandomUtil.nextFloat(jitter);
    this.speed = speed;
    this.inRange = false;
    this.enabled = true;
  }
  
  @Override
  public void reset() {
    this.inRange = false;
    this.enabled = true;
  }
  
  float dX;
  float dY;
  float distance;
  float velocityScale; 
  public void update(long time) {
    
    if (!enabled) {
      return;
    }
    
    dX = target.x + xTargetOffset - parent.x;
    dY = target.y + yTargetOffset - parent.y;
    distance = FloatMath.sqrt(dX * dX + dY * dY); 
    if (distance < waitDistance) {
      parent.velocity.x = 0;
      parent.velocity.y = 0;
      inRange = true;
      return;
    }
    
    inRange = false;
    
    velocityScale = (float) Math.min(distance, speed);
    
    parent.velocity.x = dX;
    parent.velocity.y = dY;
    parent.velocity.normalize();
    parent.velocity.x *= velocityScale;
    parent.velocity.y *= velocityScale;
  }
  
  /**
   * Disables the pather. Once triggered the parent object will no longer
   * have itself directed by the pather.
   */
  public void disable() {
    enabled = false;
  }
  
  /**
   * Allows the pather to move the parent owner.
   */
  public void enable() {
    enabled = true;
  }
  
  public boolean inWaitRange() {
    return inRange;
  }
}
