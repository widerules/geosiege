package com.geosiege.game.upgrade;

import com.geosiege.game.guns.Gun;
import com.geosiege.game.guns.GunBuilder;

public class GunDoubleUpgrade extends GunUpgrade {

  private static final String KEY = "DOUBLE_GUN_UPGRADE";
  private static final String NAME = "Dual Blaster";
  private static final int COST = 1000;

  public GunDoubleUpgrade(MoneyProvider moneyProvider) {
    super(moneyProvider, KEY, NAME, COST);
  }

  @Override
  public Gun getGun() {

    return new GunBuilder()
        .withBulletSpeed(150)
        .withCooldown(200)
        .withFireOffset(40)
        .withMultiplier(2, -2, 4)
        .build();
  }
}
