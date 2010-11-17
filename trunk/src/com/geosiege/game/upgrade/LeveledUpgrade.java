package com.geosiege.game.upgrade;

import java.util.List;

import android.content.SharedPreferences;

/**
 * An upgrade that is made of up progression of levels that are available
 * for purchase. An example would be Ship Speed or Fire Rate, which could
 * be upgraded to be slightly faster.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class LeveledUpgrade implements Upgrade {

  private MoneyProvider moneyProvider;
  private final String name;
  private final List<UpgradeLevel> levels;
  private final String key;
  private int level;
  
  public LeveledUpgrade(
      MoneyProvider moneyProvider,
      String key,
      String name,
      List<UpgradeLevel> levels) {
    this.moneyProvider = moneyProvider;
    this.key = key;
    this.name = name;
    this.levels = levels;
  }
  
  public boolean isAvailable() {
    return !isLevelMaxed();
  }
  
  public boolean isLevelMaxed() {
    return level >= levels.size() - 1;
  }
  
  public String getName() {
    return name;
  }
  
  public void save(SharedPreferences.Editor editor) {
    editor.putInt(key, level);
  }

  public void load(SharedPreferences prefs) {
    level = prefs.getInt(key, 0);
  }

  public int getLevel() {
    return level + 1;
  }

  public int getMaxLevel() {
    return levels.size();
  }
  
  protected UpgradeLevel getLevelDetails() {
    return levels.get(level);
  }

  public int getCost() {
    return levels.get(level).getCost();
  }

  public void buy() {
    if (!canAfford()) {
      return;
    }
    moneyProvider.spendMoney(getCost());
    
    if (level < levels.size() - 1) {
      level++;
    }
  }

  public boolean canAfford() {
    return getCost() <= moneyProvider.getMoney();
  }

  public boolean isBought() {
    return isLevelMaxed();
  }
}
