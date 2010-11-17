package com.geosiege.game.upgrade;

public interface MoneyProvider {
  int getMoney();
  void addMoney(int amount);
  void spendMoney(int amount);
}
