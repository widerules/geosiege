package com.geosiege.game.upgrade;

import com.geosiege.game.guns.Gun;
import com.geosiege.game.guns.GunBuilder;

public class GunTripleUpgrade extends GunUpgrade {

  private static final String KEY = "TRIPLE_GUN_UPGRADE";
  private static final String NAME = "Tri Blaster";
  private static final int COST = 2000;

  public GunTripleUpgrade(MoneyProvider moneyProvider) {
    super(moneyProvider, KEY, NAME, COST);
  }

  @Override
  public Gun getGun() {

    return new GunBuilder()
        .withBulletSpeed(150)
        .withCooldown(200)
        .withFireOffset(40)
        .withMultiplier(3, -5, 5)
        .build();
  }
}
