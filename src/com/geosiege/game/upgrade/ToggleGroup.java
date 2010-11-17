package com.geosiege.game.upgrade;

import java.util.ArrayList;
import java.util.List;

import com.geosiege.game.upgrade.ToggleUpgrade.ToggleStateListener;

/**
 * A group of upgrades in which only one upgrade can be enabled at a time.
 * If an upgrade is enabled, all others will be disabled. If an optional
 * default upgrade is enabled, it will automatically be enabled once all others
 * are disabled.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class ToggleGroup<T extends ToggleUpgrade> implements ToggleStateListener {

  ToggleUpgrade defaultUpgrade;
  List<T> upgrades;
  
  /** Create using a builder. */
  private ToggleGroup() {
    upgrades = new ArrayList<T>();
  }
  
  /** Adds an upgrade to the group. */
  public void addUpgrade(T upgrade) {
    upgrade.setListener(this);
    upgrades.add(upgrade);
  }

  /**
   * Adds a default upgrade to the group. A default upgrade is automatically
   * bought and will be enabled if none of the other upgrades within the group
   * are enabled. Adding a default upgrade is optional.
   */
  public void addDefaultUpgrade(T upgrade) {
    this.defaultUpgrade = upgrade;
    if (!upgrades.contains(upgrade)) {
      addUpgrade(upgrade);
    }
  }
  
  public void refresh() {
    // A default upgrade should always be available.
    if (!defaultUpgrade.isBought()) {
      defaultUpgrade.bought = true;
    }
    
    // If nothing is enabled yet, start the enabled one off.
    if (!anyEnabled()) {
      defaultUpgrade.enabled = true;
    }
  }
  
  /**
   * Returns true if any of the upgrades in the group are enabled.
   */
  public boolean anyEnabled() {
    return getEnabled() == null ? false : true;
  }
  
  /**
   * Returns the only enabled upgrade in the group. Null if none of the upgrades
   * are currently enabled.
   */
  public T getEnabled() {
    for (T upgrade : upgrades) {
      if (upgrade.isEquipped()) {
        return upgrade;
      }
    }
    return null;
  }

  /**
   * Listens to any of the upgrades when they are toggled by the user.
   */
  public void onToggle(ToggleUpgrade upgrade) {
    
    // If an upgrade is disabled, turn on the default option.
    if (!upgrade.isEquipped()) {
      if (defaultUpgrade != null) {
        defaultUpgrade.enabled = true;
      }
      return;
    }
    
    // If an upgrade is enabled, turn off everything else.
    for (ToggleUpgrade otherUpgrade : upgrades) {
      if (otherUpgrade != upgrade) {
        otherUpgrade.enabled = false;
      }
    }
  }
  
  /**
   * Creates a new builder.
   */
  public static <S extends ToggleUpgrade> ToggleGroupBuilder<S> builder() {
    return new ToggleGroupBuilder<S>();
  }
  
  /**
   * Builds a new toggle group.
   */
  public static class ToggleGroupBuilder<T extends ToggleUpgrade> {
    private T defaultUpgrade;
    private List<T> upgrades = new ArrayList<T>();
    
    public ToggleGroupBuilder<T> withUpgrade(T upgrade) {
      upgrades.add(upgrade);
      return this;
    }
    
    public ToggleGroupBuilder<T> withUpgrades(T... newUpgrades) {
      for (T upgrade : newUpgrades) {
        upgrades.add(upgrade);
      }
      return this;
    }
    
    public ToggleGroupBuilder<T> withDefaultUpgrade(T upgrade) {
      defaultUpgrade = upgrade;
      return this;
    }
    
    public ToggleGroup<T> build() {
      ToggleGroup<T> group = new ToggleGroup<T>();
      for(T upgrade : upgrades) {
        group.addUpgrade(upgrade);
      }
          
      if (defaultUpgrade != null) {
        group.addDefaultUpgrade(defaultUpgrade);
      }
      
      return group;
    }
  }
}
