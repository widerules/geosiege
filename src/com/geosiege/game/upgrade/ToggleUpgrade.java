package com.geosiege.game.upgrade;

import android.content.SharedPreferences;

/**
 * An upgrade that can be purchased once, then toggled on and off.
 */
public class ToggleUpgrade implements Upgrade {

  private static final String PURCHASE_SUFFIX = "-bought";
  private static final String ON_SUFFIX = "-on";

  private ToggleStateListener listener;
  private MoneyProvider moneyProvider;
  private final String name;
  private final String key;
  private final int cost;
  protected boolean bought;
  protected boolean enabled;

  public ToggleUpgrade(
      MoneyProvider moneyProvider,
      String key,
      String name,
      int cost) {

    this.moneyProvider = moneyProvider;
    this.key = key;
    this.name = name;
    this.cost = cost;
  }
  
  public boolean isAvailable() {
    return true;
  }
  
  public String getName() {
    return name;
  }
  
  public void save(SharedPreferences.Editor editor) {
    editor.putBoolean(key + PURCHASE_SUFFIX, bought);
    editor.putBoolean(key + ON_SUFFIX, enabled);
  }

  public void load(SharedPreferences prefs) {
    bought = prefs.getBoolean(key + PURCHASE_SUFFIX, false);
    enabled = prefs.getBoolean(key + ON_SUFFIX, false);
  }

  public int getCost() {
    return cost;
  }

  public void buy() {
    if (!canAfford()) {
      return;
    }
    moneyProvider.spendMoney(getCost());
    bought = true;
    equip();
  }

  public boolean canAfford() {
    return isBought() || getCost() <= moneyProvider.getMoney();
  }
  
  public boolean isEquipped() {
    return enabled;
  }
  
  public boolean isBought() {
    return bought;
  }
  
  public void equip() {
    enabled = true;
    notifyListeners();
  }

  public void unequip() {
    enabled = false;
    notifyListeners();
  }
  
  public void setListener(ToggleStateListener listener) {
    this.listener = listener;
  }

  private void notifyListeners() {
    if (listener != null) {
      listener.onToggle(this);
    }
  }

  public static interface ToggleStateListener {
    void onToggle(ToggleUpgrade upgrade);
  }
}
