package com.geosiege.game.upgrade;

import com.geosiege.game.guns.Gun;

public abstract class GunUpgrade extends ToggleUpgrade {
  public GunUpgrade(MoneyProvider moneyProvider, String key, String name, int cost) {
    super(moneyProvider, key, name, cost);
  }

  public abstract Gun getGun();
}
