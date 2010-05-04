package com.geosiege.test.collision;

import android.test.AndroidTestCase;

import com.geosiege.common.PhysicalObject;
import com.geosiege.common.collision.GridSpot;

public class GridSpotTest extends AndroidTestCase {

  GridSpot spot;
  PhysicalObject dummy;
  PhysicalObject dummy2;
  
  @Override
  public void setUp() {
    spot = new GridSpot();
    dummy = new PhysicalObject(0, 0);
    dummy2 = new PhysicalObject(2, 2);
  }
  
  public void testAdd() {
    assertEquals(0, spot.numObjects);
    assertNotSame("Raw array was not initialized.", 0, spot.objects.length);

    spot.add(dummy);
   
    assertEquals(1, spot.numObjects);
    assertEquals(dummy, spot.objects[0]);
  }
  
  public void testAdd_cantAddSameItemTwice() {
    spot.add(dummy);
    spot.add(dummy);
    assertEquals(1, spot.numObjects);
  }
  
  public void testRemove() {
    spot.add(dummy);
    spot.remove(dummy);
    assertEquals(0, spot.numObjects);
    
    spot.add(dummy);
    spot.add(dummy2);
    assertEquals(2, spot.numObjects);
    spot.remove(dummy);
    spot.remove(dummy2);
    assertEquals(0, spot.numObjects);
  }
  
  public void testGrow() {
    
    // Insert enough objects to almost, but not quite, force it to grow.
    int initialSize = spot.objects.length;
    for (int i = 0; i < initialSize; i++) {
      spot.add(new PhysicalObject(i, i));
    }
    
    assertEquals(initialSize, spot.numObjects);
    assertEquals(initialSize, spot.objects.length);
    
    spot.add(dummy);
    
    // Verify that it grew.
    assertNotSame(initialSize, spot.objects.length);
    
    assertEquals(dummy, spot.objects[initialSize]);
  }
}
