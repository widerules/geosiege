package com.geosiege.common.util;

import java.util.ArrayList;

import android.graphics.Canvas;

import com.geosiege.common.Component;
import com.geosiege.common.GameObject;

public class ComponentManager extends GameObject {

  ArrayList<Component> components;
  GameObject parent;
  
  
  public ComponentManager(GameObject parent) {
    this.components = new ArrayList<Component>();
    this.parent = parent;
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
  
  public void add(Component component) {
    component.parent = this;
    components.add(component);
  }
}
