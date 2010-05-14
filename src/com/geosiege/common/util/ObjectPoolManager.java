package com.geosiege.common.util;

import android.graphics.Canvas;

import com.geosiege.common.GameObject;
import com.geosiege.common.util.ObjectPool.ObjectBuilder;

/**
 * Manages a pool of GameObject's, handling the task of updating, drawing,
 * and reclaiming objects for the pool once they are no longer active.
 * 
 * The PoolManager will create its own pool given the appropriate parameters.
 * 
 * If custom objects must be made to populate the pool, a 
 * {@link ObjectBuilder} may be used.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 *
 * @param <T> The object that will make up the pool.
 */
public class ObjectPoolManager<T extends GameObject> extends GameObject {
  
  /** Default pool size to use if none is specified. */
  private static final int DEFAULT_POOL_SIZE = 50;
  
  /** A pool of objects that can be reused. */
  public ObjectPool<T> pool;
  
  /** The class that will populate the pool. */
  private final Class<T> clazz;
  
  /** A custom builder that can create each individual object in the pool. */
  private final ObjectBuilder<T> builder;
  
  /** The maximum number of objects to keep alive within the pool. */
  private final int poolSize;
  
  /**
   * Creates a new pool of the given class. Objects in the pool will be 
   * created by calling their default constructor.
   */
  public ObjectPoolManager(Class<T> clazz) {
    this(clazz, DEFAULT_POOL_SIZE);
  }
  
  /**
   * Creates a new pool of the given class and the given size. Objects in the 
   * pool will be created by calling their default constructor. 
   */
  public ObjectPoolManager(Class<T> clazz, int poolSize) {
    this(clazz, poolSize, null);
  }
  
  /**
   * Creates a new pool. Objects in the pool are created using the provided 
   * builder.
   */
  public ObjectPoolManager(Class<T> clazz, int poolSize, ObjectBuilder<T> builder) {
    this.clazz = clazz;
    this.poolSize = poolSize;
    this.builder = (builder != null ? builder : createDefaultBuilder());
    
    createPool();
  }
  
  /**
   * Create a simple builder that does nothing but call the default constructor
   * of the pool object class.
   */
  private ObjectBuilder<T> createDefaultBuilder() {
     return new ObjectBuilder<T>() {
      @Override
      public T get(int count) {
        
        try {
          T gameObject = clazz.newInstance();
          gameObject.active = false;
          return gameObject;
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InstantiationException e) {
          e.printStackTrace();
        }
        
        return null;
      }
     };
  }
  
  private void createPool() {
    pool = new ObjectPool<T>(clazz, poolSize, builder);
  }
  
  /**
   * Returns a single object from the pool. The object will automatically
   * be restored to the pool once it's active property is set to false.
   */
  public T take() {
    return pool.take();
  }
  
  /**
   * Draws all objects in the pool. Skips non-active objects.
   */
  public void draw(Canvas canvas) {
    T gameObject;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      gameObject = pool.items[i];
      if (gameObject.active)
        gameObject.draw(canvas);
    }
  }
  
  /**
   * Updates all objects in the pool. Skips non-active objects.
   */
  public void update(long time) {
    T gameObject;
    for ( int i = 0 ; i < pool.items.length ; i++) {
      gameObject = pool.items[i];
      if (gameObject.active) {
        gameObject.update(time);
        if (!gameObject.active) {
          pool.restore(gameObject);
        }
      }
    }
  }
  
  /**
   * Sets all items in the pool to the active state.
   */
  public void activateAllItemsInPool() {
    for ( int i = 0 ; i < pool.items.length ; i++) {
      pool.items[i].active = true;
    }
  }
  
  /**
   * Forces all objects to be reclaimed and restored back into the pool.
   */
  public void reclaimPool() {
    for ( int i = 0 ; i < pool.items.length ; i++) {
      pool.restore(pool.items[i]);
    }
  }
}
