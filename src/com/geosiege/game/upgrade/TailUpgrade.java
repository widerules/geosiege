package com.geosiege.game.upgrade;

public class TailUpgrade extends ToggleUpgrade {

  private static final String KEY = "TAIL_UPGRADE";
  private static final String NAME = "Fiery";
  private static final int COST = 500;
  
  public TailUpgrade(MoneyProvider moneyProvider) {
    super(moneyProvider, KEY, NAME, COST);
  }
}
