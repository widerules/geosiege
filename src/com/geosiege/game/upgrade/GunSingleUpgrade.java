package com.geosiege.game.upgrade;

import com.geosiege.game.guns.Gun;
import com.geosiege.game.guns.GunBuilder;

public class GunSingleUpgrade extends GunUpgrade {

  private static final String KEY = "SINGLE_GUN_UPGRADE";
  private static final String NAME = "Pea Shooter";
  private static final int COST = 5;

  public GunSingleUpgrade(MoneyProvider moneyProvider) {
    super(moneyProvider, KEY, NAME, COST);
  }

  public Gun getGun() {
    return new GunBuilder()
        .withBulletSpeed(150)
        .withCooldown(200)
        .withFireOffset(40)
        .build();
  }
}
