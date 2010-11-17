package com.geosiege.game.upgrade;

import java.util.ArrayList;
import java.util.List;

public class SpeedUpgrade extends LeveledUpgrade {

  private static final String KEY = "UPGRADE_SPEED";
  private static final String NAME = "Ship Speed";
  private static final List<UpgradeLevel> LEVELS = new ArrayList<UpgradeLevel>();
  static {
    LEVELS.add(new UpgradeLevel(200, 80));
    LEVELS.add(new UpgradeLevel(300, 85));
    LEVELS.add(new UpgradeLevel(400, 90));
    LEVELS.add(new UpgradeLevel(500, 95));
    LEVELS.add(new UpgradeLevel(600, 100));
    LEVELS.add(new UpgradeLevel(700, 110));
    LEVELS.add(new UpgradeLevel(800, 120));
    LEVELS.add(new UpgradeLevel(900, 130));
    LEVELS.add(new UpgradeLevel(1000, 140));
    LEVELS.add(new UpgradeLevel(1000, 150));
  }
  
  public SpeedUpgrade(MoneyProvider moneyProvider) {
    super(moneyProvider, KEY, NAME, LEVELS);
  }
  
  public float get() {
    return getLevelDetails().getValue();
  }
}
