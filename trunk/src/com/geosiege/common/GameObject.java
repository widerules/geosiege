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

package com.geosiege.common;

import java.util.ArrayList;



import android.graphics.Canvas;


public class GameObject {
  
  /** Whether an object should be actively drawn and updated. */
  public boolean active = true;
  
  /** Whether an object can be restored to an object pool. */
  public boolean canRecycle = false;
  
  /** Whether an object was obtained from an object pool. */
  public boolean taken = false;
  
  ArrayList<Component> components;
   
  public GameObject() {
    components = new ArrayList<Component>();
  }
  
  public void draw(Canvas canvas) {
    int size = components.size();
    Component component;
    for ( int i = 0 ; i < size ; i++) {
      component = components.get(i);
      synchronized (component) {
        component.draw(canvas);
      }
    }
  }
  
  public void update(long time) {
    int size = components.size();
    Component component;
    for ( int i = 0 ; i < size ; i++) {
      component = components.get(i);
      synchronized (component) {
        component.update(time);
      }
    }
  }
  
  public void addComponent(Component component) {
    component.parent = this;
    components.add(component);
  }
  
  public void enable() {
    active = true;
    canRecycle = false;
  }
  
  /**
   * Disables an object and clears it to be restored to an object pool.
   */
  public void kill() {
    active = false;
    canRecycle = true;
  }
}
