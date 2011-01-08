package com.geosiege.game.upgrade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;

import com.geosiege.game.guns.Gun;

public class Upgrades implements MoneyProvider {

  private static final String PREFERENCES_FILE_NAME = "GeoSiege.Upgrades";
  private static final String KEY_MONEY = "MONEY";

  private static final String GROUP_SHIP = "Ship Upgrades";
  private static final String GROUP_GUNS = "Guns";
  private static final String GROUP_ENGINE_TRAIL = "Engine Trails";
  
  private SharedPreferences prefs;
  private SharedPreferences.Editor editor;
  
  private SpeedUpgrade speedUpgrade = new SpeedUpgrade(this);
  private TailUpgrade tailUpgrade = new TailUpgrade(this);
  private GunUpgrade singleGun = new GunSingleUpgrade(this);
  private GunUpgrade doubleGun = new GunDoubleUpgrade(this);
  private GunUpgrade triGun = new GunTripleUpgrade(this);
  private ToggleGroup<GunUpgrade> gunUpgrades = ToggleGroup.<GunUpgrade>builder()
      .withUpgrades(singleGun, doubleGun, triGun)
      .withDefaultUpgrade(singleGun)
      .build();

  private Map<String, List<Upgrade>> upgrades;
  
  private int money;

  public Upgrades(Activity activity) {
    this(activity.getSharedPreferences(PREFERENCES_FILE_NAME, 0));
  }

  public Upgrades(SharedPreferences prefs) {  
    this.prefs = prefs;
    this.editor = prefs.edit();
    createUpgradesList();
    load();
  }

  public Map<String, List<Upgrade>> getUpgrades() {
    return upgrades;
  }

  private void createUpgradesList() {
    upgrades = new LinkedHashMap<String, List<Upgrade>>();
    upgrades.put(GROUP_SHIP, Arrays.<Upgrade>asList(speedUpgrade));
    upgrades.put(GROUP_GUNS, Arrays.<Upgrade>asList(singleGun, doubleGun, triGun));
    upgrades.put(GROUP_ENGINE_TRAIL, Arrays.<Upgrade>asList(tailUpgrade));
  }
  
  private List<Upgrade> getUpgradesAsList() {
    List<Upgrade> allUpgrades = new ArrayList<Upgrade>();
    for (List<Upgrade> subList : upgrades.values()) {
      allUpgrades.addAll(subList);
    }
    return allUpgrades;
  }
  
  public void load() {
    for (Upgrade upgrade : getUpgradesAsList()) {
      upgrade.load(prefs);
    }
    money = prefs.getInt(KEY_MONEY, 0);
    
    // Ensure that groups have defaults enabled correctly.
    gunUpgrades.refresh();
  }
  
  public void save() {
    for (Upgrade upgrade : getUpgradesAsList()) {
      upgrade.save(editor);
    }
    editor.putInt(KEY_MONEY, money);
    editor.commit();
  }
  
  // Expose current upgrades to game.

  public float getSpeed() {
    return speedUpgrade.get();
  }

  public Gun getGun() {
    return gunUpgrades.getEnabled().getGun();
  }

  public boolean isTailEnabled() {
    return tailUpgrade.isEquipped();
  }
  
  // MoneyProvider Interface

  public int getMoney() {
    return money;
  }

  public void addMoney(int amount) {
    money += amount;
  }

  public void spendMoney(int amount) {
    if (amount > money)  {
      throw new RuntimeException("Attempted to spend more money that you had!");
    }
    money -= amount;
  }
}
