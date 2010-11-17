package com.geosiege.game;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.geosiege.game.core.GameState;
import com.geosiege.game.upgrade.Upgrade;
import com.geosiege.game.upgrade.UpgradeListMenuAdapter;
import com.zeddic.game.common.ui.SeparatedListAdapter;

public class UpgradesActivity extends Activity {

  private ListView listView;
  private TextView moneyText;
  private SeparatedListAdapter adapter;
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    GameState.setup(this);
    GameState.analytics.trackPageView("/upgrades");
    
    setContentView(R.layout.upgrades);
    
    adapter = new SeparatedListAdapter(this);
    Map<String, List<Upgrade>> upgrades = GameState.upgrades.getUpgrades();
    for (String group : upgrades.keySet()) {
      adapter.addSection(group, getUpgradesAdapter(upgrades.get(group)));
    }
    
    moneyText = (TextView) findViewById(R.id.upgradeMoneyText);      
    listView = (ListView) findViewById(R.id.upgradesListView);
    listView.setAdapter(adapter);
    
    refresh();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    GameState.cleanup();
  }
  
  @Override
  public void onResume() {
    super.onResume();
    refresh();
  }
  
  private void refresh() {
    refreshMoneyText();
    adapter.notifyDataSetChanged();
  }
  
  private void refreshMoneyText() {
    moneyText.setText("$"+ UpgradeListMenuAdapter.MONEY_FORMAT.format(
        GameState.upgrades.getMoney()));
  }
  
  private UpgradeListMenuAdapter getUpgradesAdapter(List<Upgrade> upgrades) {

    // Create the adapter.
    UpgradeListMenuAdapter adapter =  new UpgradeListMenuAdapter(
        this, R.layout.upgraderow, GameState.upgrades, upgrades);
    
    // Listen to when it requests any UI refreshes, such as when
    // an item is bought.
    adapter.addRefreshRequestListener(
        new Runnable() {
          public void run() {
            refresh();
          }
        });

    return adapter;
  }
}