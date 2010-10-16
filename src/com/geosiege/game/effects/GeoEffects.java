package com.geosiege.game.effects;

import com.zeddic.game.common.PhysicalObject;
import com.zeddic.game.common.effects.Effects;

public class GeoEffects {

  private static final GeoEffects singleton = new GeoEffects();

  private final Effects effects;
  
  private GeoEffects() {
    effects = Effects.get();
    createPools();
  } 
  
  private void createPools() {
    effects.createSupply(PewExplosion.class, 50);
    effects.createSupply(ShockwaveExplosion.class, 100);
    effects.createSupply(TeleportExplosion.class, 50);
  }
  
  public static GeoEffects get() {
    return singleton;
  }
  
  public PewExplosion explode(float x, float y) {
    
    PewExplosion explosion = effects.take(PewExplosion.class);
    if (explosion == null)
      return null;
    
    explosion.x = x;
    explosion.y = y;
    explosion.ignite();
    return explosion;
  }
  
  public ShockwaveExplosion shockwave(float x, float y) {
    
    ShockwaveExplosion explosion = effects.take(ShockwaveExplosion.class);
    if (explosion == null)
      return null;
    
    explosion.x = x;
    explosion.y = y;
    explosion.ignite();
    return explosion;
  }
  
  public TeleportExplosion teleport(float x, float y, PhysicalObject zoomTo) {
    TeleportExplosion explosion = effects.take(TeleportExplosion.class);
    
    if (explosion == null)
      return null;
    
    explosion.x = x;
    explosion.y = y;
    explosion.ignite(zoomTo);
    return explosion;
  }
}
