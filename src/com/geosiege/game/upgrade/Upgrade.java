package com.geosiege.game.upgrade;

import android.content.SharedPreferences;

public interface Upgrade {
  void buy();
  boolean canAfford();
  boolean isBought();
  boolean isAvailable();
  void load(SharedPreferences prefs);
  void save(SharedPreferences.Editor editor);
  int getCost();
  String getName();
}
