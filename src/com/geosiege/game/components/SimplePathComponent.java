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

import com.geosiege.common.Component;
import com.geosiege.common.PhysicalObject;
import com.geosiege.common.util.RandomUtil;

public class SimplePathComponent extends Component {

  public PhysicalObject parent;
  public PhysicalObject target;
  public float xTargetOffset;
  public float yTargetOffset;
  public float waitDistance;
  
  public SimplePathComponent(PhysicalObject parent, PhysicalObject target, float jitter) {
    this.parent = parent;
    this.target = target;
    this.xTargetOffset = RandomUtil.nextFloat(-jitter, jitter);
    this.yTargetOffset = RandomUtil.nextFloat(-jitter, jitter);
    this.waitDistance = 150 + RandomUtil.nextFloat(jitter);
  }
  
  float dX;
  float dY;
  float distance;
  float velocityScale; 
  public void update(long time) {
    dX = target.x + xTargetOffset - parent.x;
    dY = target.y + yTargetOffset - parent.y;
    distance = FloatMath.sqrt(dX * dX + dY * dY); 
    if (distance < waitDistance) {
      parent.velocity.x = 0;
      parent.velocity.y = 0;
      return;
    }
    
    velocityScale = (float) Math.min(distance, 30);
    
    parent.velocity.x = dX;
    parent.velocity.y = dY;
    parent.velocity.normalize();
    parent.velocity.x *= velocityScale;
    parent.velocity.y *= velocityScale;
  }
}
